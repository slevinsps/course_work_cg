package CG_editor;

public class ColorCG {
    public float red, green, blue, specular, special, refr_koef, opacity;
    public ColorCG () {
	red = 0.5f;
	green = 0.5f;
	blue = 0.5f;
        specular = 0;
        special = 0;
        refr_koef = 0;
        opacity = 0;
    } 

    
    public ColorCG (float r, float g, float b) {
        red = r;
	green = g;
	blue = b;
        specular = 0;
	special = 0;
        refr_koef = 0;
        opacity = 0; 
    }
    
    public ColorCG (float r, float g, float b, float specular_v, float s, float refr, float opacity_v) {
        red = r;
	green = g;
	blue = b;
        specular = specular_v;
	special = s;
        refr_koef = refr;
        opacity = opacity_v;
    }

    
    public float getColorRed() { return red; }
    public float getColorGreen() { return green; }
    public float getColorBlue() { return blue; }
    public float getColorSpecial() { return special; }
    public float getColorRefr() { return refr_koef; }

    public float setColorRed(float redValue) { red = redValue; return 0; }
    public float setColorGreen(float greenValue) { green = greenValue; return 0;}
    public float setColorBlue(float blueValue) { blue = blueValue; return 0;}
    public float setColorSpecial(float specialValue) { special = specialValue; return 0;}
    public float setColorRefr(float specialValue) { refr_koef = specialValue; return 0;}

    public ColorCG ColorMul(float scalar) {
            return new ColorCG(red*scalar, green*scalar, blue*scalar, specular, special, this.refr_koef, this.opacity);
    }

    public ColorCG ColorAdd(ColorCG color) {
            return new ColorCG(red + color.getColorRed(), green + color.getColorGreen(), blue + color.getColorBlue(),specular, special, this.refr_koef, this.opacity);
    }

    public ColorCG ColorMul(ColorCG color) {
            return new ColorCG(red*color.getColorRed(), green*color.getColorGreen(), blue*color.getColorBlue(),specular, special, this.refr_koef, this.opacity);
    }

    public ColorCG Limit() {
            float alllight = red + green + blue;
            float excesslight = alllight - 3;
            if (excesslight > 0) {
                red = red + excesslight*(red/alllight);
                green = green + excesslight*(green/alllight);
                blue = blue + excesslight*(blue/alllight);
            }
            if (red > 1) {red = 1;}
            if (green > 1) {green = 1;}
            if (blue > 1) {blue = 1;}
            if (red < 0) {red = 0;}
            if (green < 0) {green = 0;}
            if (blue < 0) {blue = 0;}

            return new ColorCG(red, green, blue, specular, special, this.refr_koef, this.opacity);
    }
}
