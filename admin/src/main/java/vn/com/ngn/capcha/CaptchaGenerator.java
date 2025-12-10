package vn.com.ngn.capcha;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.BackgroundProducer;
import cn.apiclub.captcha.backgrounds.TransparentBackgroundProducer;
import cn.apiclub.captcha.noise.CurvedLineNoiseProducer;
import cn.apiclub.captcha.noise.NoiseProducer;
import cn.apiclub.captcha.text.producer.DefaultTextProducer;
import cn.apiclub.captcha.text.producer.TextProducer;
import cn.apiclub.captcha.text.renderer.DefaultWordRenderer;
import cn.apiclub.captcha.text.renderer.WordRenderer;

public class CaptchaGenerator {

	private BackgroundProducer backgroundProducer;
    private TextProducer textProducer;
    private WordRenderer wordRenderer;
    private NoiseProducer noiseProducer;
    
    public Captcha createCaptcha(int width,int height) {
    	return new Captcha.Builder(width, height)
    			.addBackground()
    			.addText()
    			.addNoise()
    			.build();
    }
    
    public String getGreeting() {
    	return "captcha";
    }
    
    public CaptchaGenerator() {
        if(this.backgroundProducer == null) {
            this.backgroundProducer = new TransparentBackgroundProducer();
        }
        if(this.textProducer==null) {
            this.textProducer = new DefaultTextProducer();
        }
        if(this.wordRenderer==null) {
            this.wordRenderer = new DefaultWordRenderer();
        }
        if(this.noiseProducer==null) {
            this.noiseProducer = new CurvedLineNoiseProducer();
        }

    }
}
