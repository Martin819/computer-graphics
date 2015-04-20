#version 330
in vec2 vertInPosition;
out vec4 outColor; // vystup z fragment shaderu
uniform float viewMethod;
uniform float imgWidth;
uniform float imgHeight;
uniform float effectIntensity;
uniform sampler2D texture;

const float gaussBlurSize = 1.0/200.0;
const vec3 LumCoeff = vec3(0.2125, 0.7154, 0.0721);

float sharpen_kernel[9];
vec2 sharpen_offset[9];

float sharpen_step_w = 1.0/imgWidth;
float sharpen_step_h = 1.0/imgHeight;

float threshold(in float thr1, in float thr2 , in float val) {
 if (val < thr1) {return 0.0;}
 if (val > thr2) {return 1.0;}
 return val;
}

// averaged pixel intensity from 3 color channels
float avg_intensity(in vec4 pix) {
 return (pix.r + pix.g + pix.b)/3.;
}

vec4 get_pixel(in vec2 coords, in float dx, in float dy) {
 return texture2D(texture,coords + vec2(dx, dy));
}

// returns pixel color
float IsEdge(in vec2 coords){
  float dxtex = 1.0 / 512.0 /*image width*/;
  float dytex = 1.0 / 512.0 /*image height*/;
  float pix[9];
  int k = -1;
  float delta;

  // read neighboring pixel intensities
  for (int i=-1; i<2; i++) {
   for(int j=-1; j<2; j++) {
    k++;
    pix[k] = avg_intensity(get_pixel(coords,float(i)*dxtex,
                                          float(j)*dytex));
   }
  }

  // average color differences around neighboring pixels
  delta = (abs(pix[1]-pix[7])+
          abs(pix[5]-pix[3]) +
          abs(pix[0]-pix[8])+
          abs(pix[2]-pix[6])
           )/4.;

  return threshold(0.3,0.45,clamp(1.8*delta,0.0,1.0));
}

void main()
{
  vec2 newPosition = (vertInPosition+1)/2;
  if(viewMethod==1){
      //edge detection
	  float c = IsEdge(newPosition);
	  vec4 color = vec4(c,c,c,1.0);
	  outColor = color;
  }else if(viewMethod==2){
      //gaussian blur
  	  vec4 sum = vec4(0.0);
      float blurSize=gaussBlurSize*effectIntensity;
	  // blur in y (vertical)
	  // take nine samples, with the distance blurSize between them
	  sum += texture2D(texture, vec2(newPosition.x - 4.0*blurSize, newPosition.y)) * 0.05;
	  sum += texture2D(texture, vec2(newPosition.x - 3.0*blurSize, newPosition.y)) * 0.09;
	  sum += texture2D(texture, vec2(newPosition.x - 2.0*blurSize, newPosition.y)) * 0.12;
	  sum += texture2D(texture, vec2(newPosition.x - blurSize, newPosition.y)) * 0.15;
	  sum += texture2D(texture, vec2(newPosition.x, newPosition.y)) * 0.16;
	  sum += texture2D(texture, vec2(newPosition.x + blurSize, newPosition.y)) * 0.15;
	  sum += texture2D(texture, vec2(newPosition.x + 2.0*blurSize, newPosition.y)) * 0.12;
	  sum += texture2D(texture, vec2(newPosition.x + 3.0*blurSize, newPosition.y)) * 0.09;
	  sum += texture2D(texture, vec2(newPosition.x + 4.0*blurSize, newPosition.y)) * 0.05;
 
      outColor = sum;
  }else if(viewMethod == 3){
        //contrast
        vec3 color = vec3(texture2D(texture,newPosition));
        vec3 AvgLumin = vec3(0.25, 0.25, 0.25);
        vec3 intensity = vec3(dot(color, LumCoeff));
        // could substitute a uniform for this 1. and have variable saturation
        vec3 satColor = mix(intensity, color, 1.);
        vec3 conColor = mix(AvgLumin, satColor, effectIntensity+1);

        outColor = vec4(conColor, 1);
  }else if(viewMethod == 4){
	    //sobel
	    vec4 s1 = texture(texture, newPosition - 1.0 / 300.0 - 1.0 / 200.0);
		vec4 s2 = texture(texture, newPosition + 1.0 / 300.0 - 1.0 / 200.0);
		vec4 s3 = texture(texture, newPosition - 1.0 / 300.0 + 1.0 / 200.0);
		vec4 s4 = texture(texture, newPosition + 1.0 / 300.0 + 1.0 / 200.0);
		vec4 sx = 4.0 * ((s4 + s3) - (s2 + s1));
		vec4 sy = 4.0 * ((s2 + s4) - (s1 + s3));
		vec4 sobel = sqrt(sx * sx + sy * sy);
		outColor = sobel;
  }else if(viewMethod == 5){
  		//grayscale
  		outColor = texture(texture,newPosition);
		float avg = 0.2126 * outColor.r + 0.7152 * outColor.g + 0.0722 * outColor.b;
		outColor = vec4(avg, avg, avg, 1.0);
  }else if(viewMethod == 6){
        //brightness
        outColor = texture2D(texture, newPosition);
        // Apply brightness.
  		outColor.rgb += effectIntensity;
  		outColor.rgb *= outColor.a;
  }else if(viewMethod == 7){
    //hue
    const vec4  kRGBToYPrime = vec4 (0.299, 0.587, 0.114, 0.0);
    const vec4  kRGBToI     = vec4 (0.596, -0.275, -0.321, 0.0);
    const vec4  kRGBToQ     = vec4 (0.212, -0.523, 0.311, 0.0);

    const vec4  kYIQToR   = vec4 (1.0, 0.956, 0.621, 0.0);
    const vec4  kYIQToG   = vec4 (1.0, -0.272, -0.647, 0.0);
    const vec4  kYIQToB   = vec4 (1.0, -1.107, 1.704, 0.0);

    // Sample the input pixel
    vec4 color   = texture2D(texture, newPosition);

    // Convert to YIQ
    float   YPrime  = dot (color, kRGBToYPrime);
    float   I      = dot (color, kRGBToI);
    float   Q      = dot (color, kRGBToQ);

    // Calculate the hue and chroma
    float   hue     = atan (Q, I);
    float   chroma  = sqrt (I * I + Q * Q);

    // Make the user's adjustments
    hue += effectIntensity;

    // Convert back to YIQ
    Q = chroma * sin (hue);
    I = chroma * cos (hue);

    // Convert back to RGB
    vec4    yIQ   = vec4 (YPrime, I, Q, 0.0);
    color.r = dot (yIQ, kYIQToR);
    color.g = dot (yIQ, kYIQToG);
    color.b = dot (yIQ, kYIQToB);

    // Save the result
    outColor=color;
  }else{
  	  outColor=texture2D(texture, newPosition);
  }
}