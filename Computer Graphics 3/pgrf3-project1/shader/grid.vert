#version 330
#define PI 3.141592
in vec2 inPosition; // vstup z vertex bufferu
out vec3 normal; // vystup do dalsich casti retezce
out vec3 lightVec;
out vec3 eyeVec;
out vec3 vertColor;
out vec2 vertInPosition; 
uniform mat4 mat;
uniform vec3 lightPos;
uniform vec3 eyePos;
uniform float renderFunction;
uniform float lightPerVertex;
uniform float useTexture;
uniform float mappingType;

/* definice funkci */
/* kartezske */ 
vec3 funcSedlo(vec2 inPos) {
	float s = inPos.x * 2 - 1;
	float t = inPos.y * 2 - 1;
	return vec3(s,t,s*s - t*t);
}

vec3 funcSnake(vec2 inPos){
	float s = inPos.x;
	float t = (2 * PI * inPos.y);
	return vec3((1-s)*(3+cos(t))*cos(2*PI*s),(1-s)*(3+cos(t))*sin(2*PI*s),6*s+(1-s)*sin(t))/3;
}

vec3 funcCone(vec2 inPos){
	float s = (PI * 0.5 - PI * inPos.x *2);
	float t = inPos.y * 2 -1; 
	
	return vec3(t*cos(s),
				t*sin(s),
				t
	);
}
/* cylindricke */
vec3 funcSombrero(vec2 inPos){
	float s = (PI * 0.5 - PI * inPos.x *2);
	float t = (2 * PI * inPos.y);
	float r = t;
	float th = s;
	return vec3(
			r*cos(th),
			r*sin(th),
			2*sin(t))/2;
}

vec3 funcMushroom(vec2 inPos){
	float s= PI * 0.5 - PI * inPos.x * 2;
	float t = PI * inPos.y * 2;
	
	float r = (1+max(sin(t),0))*2;
	float th = s;	
    return vec3(
    	r*cos(th),
    	r*sin(th),
    	3-t)/2; 
}

vec3 funcGlass(vec2 inPos){
	float s= PI * 0.5 - PI * inPos.x * 2;
	float t= PI * 0.5 - PI * inPos.y * 2;
	
	float r = 1 + cos(t);
	float th = s;
	
	return vec3(
		r*cos(th),
		r*sin(th),
		t);
}

/* sfericke */
vec3 funcSphere(vec2 inPos){
	float s = PI * 0.5 - PI * inPos.x;
	float t = 2* PI * inPos.y;
	float r = 2;	
	return vec3(cos(t) * cos(s) * r,
			sin(t) * cos(s) * r, 
			sin(s) * r);
} 
vec3 funcElHead(vec2 inPos){
	float s = PI * 0.5 - PI * inPos.x;
	float t = 2* PI * inPos.y;
	
	float r = 3+cos(4*s);
	float ph = t;
	float th = s;
	
	return vec3(cos(th) * sin(ph) * r,
			sin(th) * sin(ph) * r, 
			cos(ph) * r)/2;
}

vec3 funcAlien(vec2 inPos){
	float s = PI * 0.5 - PI * inPos.x;
	float t = 2* PI * inPos.y;
	
	float r=2+cos(2*t)*sin(s);
	float ph = t;
	float th = s;

	return vec3(cos(th) * sin(ph) * r,
			sin(th) * sin(ph) * r, 
			cos(ph) * r)/2;
}

/* ridici funkce */
vec3 paramPos(vec2 inPos){
	if(renderFunction == 0){
		return funcSedlo(inPos);
	}else if(renderFunction == 1){
		return funcSnake(inPos);
	}else if(renderFunction == 2){
		return funcCone(inPos);
	}else if(renderFunction == 3){
		return funcSombrero(inPos);
	}else if(renderFunction == 4){
		return funcMushroom(inPos);
	}else if(renderFunction == 5){
		return funcGlass(inPos);
	}else if(renderFunction == 6){
		return funcSphere(inPos);
	}else if(renderFunction == 7){
		return funcElHead(inPos);
	}else if(renderFunction == 8){
		return funcAlien(inPos);
	}
}
vec3 paramNormal(vec2 inPos) {
   float delta = 0.001; 
   vec3 tx = (paramPos(inPos + vec2(delta, 0)) - paramPos(inPos - vec2(delta, 0)));
   vec3 ty = (paramPos(inPos + vec2(0, delta)) - paramPos(inPos - vec2(0, delta)));
   return cross(tx,ty);
} 
mat3 paramTangent(vec2 inPos){
	float delta = 0.001;
	vec3 tx = paramPos(inPos + vec2(delta,0)) - paramPos(inPos - vec2(delta,0));
	vec3 ty = paramPos(inPos + vec2(0,delta)) - paramPos(inPos - vec2(0,delta));
	tx= normalize(tx);
	ty = normalize(ty);
	vec3 tz = cross(tx,ty);
	ty = cross(tz,tx);
	return mat3(tx,ty,tz);
}
void main() {
	vec3 position = paramPos(inPosition);
	gl_Position = mat * vec4(position,1.0);
	normal = normalize(paramNormal(inPosition));
	lightVec = normalize(lightPos - position);
	vertInPosition = inPosition;
	eyeVec = normalize(eyePos - position);
	if(mappingType==2){
		/* parallax mapping */
		mat3 tbn = paramTangent(inPosition);
	    // not working :-( eyeVec*=tbn;
	}
	
	if(lightPerVertex == 1){
		/* osvetleni per vertex */
		vec3 norm = normal;
		float diff = dot(norm, lightVec);
		diff = max(0,diff);
		vec3 halfVec = normalize(normalize(eyeVec) + lightVec);
		float spec = dot(norm, halfVec);
		spec = max(0,spec);
		spec = pow(spec,10);
		float ambient = 0.1;
		if(useTexture == 1){
			vertColor=vec3(1,1,1) * (min(ambient + diff,1)) + vec3(1,1,1) * spec; 
		}else{
			vertColor=vec3(inPosition,0) * (min(ambient + diff,1)) + vec3(1,1,1) * spec;
		}
	}
} 
