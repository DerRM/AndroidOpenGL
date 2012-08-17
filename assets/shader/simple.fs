precision mediump float;

uniform vec3 uLightPos;
uniform vec4 vColor;

varying vec3 vPosition;
varying vec3 vNormal;

void main()
{
	float distance = length(uLightPos - vPosition);
	
	vec3 lightVector = normalize(uLightPos - vPosition);
	
	float diffuse = max(dot(vNormal, lightVector), 0.0);
	diffuse = diffuse * (1.0 / distance);
	diffuse = diffuse + 0.2;
	
	gl_FragColor = (vColor * diffuse);
}