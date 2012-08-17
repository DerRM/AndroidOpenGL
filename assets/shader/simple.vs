uniform mat4 uMVPMatrix;
uniform mat4 uMVMatrix;

attribute vec4 aPosition;
attribute vec3 aNormal;
attribute vec2 aTexCoord;

varying vec3 vPosition;
varying vec3 vNormal;
varying vec2 vTexCoord;

void main() 
{
	vPosition = vec3(uMVMatrix * aPosition);
	
    vTexCoord = aTexCoord;
    
	vNormal = vec3(uMVMatrix * vec4(aNormal, 0.0));

	gl_Position = uMVPMatrix * aPosition;
	//gl_PointSize = 5.0;
}