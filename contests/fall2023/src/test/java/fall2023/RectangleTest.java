package fall2023;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class RectangleTest {

  
  
  @Test
  void outsideIs0Coverage() throws Exception {
    Rectangle rect = new Rectangle(0, 0, 1000, 1000);
    double cover = rect.calculateCircleCoverage(2000, 2000, 999);
    
    assertThat(cover).isEqualTo(0);
  }

  @Test
  void rectInsideBigCircleIs100() throws Exception {
    Rectangle rect = new Rectangle(0, 0, 20, 20);
    double cover = rect.calculateCircleCoverage(0, 0, 999);
    
    assertThat(cover).isEqualTo(100);
  }

  
  @Test
  @Disabled
  void onOneCorner() throws Exception {
    Rectangle rect = new Rectangle(0, 0, 100, 100);
    double cover = rect.calculateCircleCoverage(0, 0, 100);
    
    assertThat(cover).isEqualTo((Math.PI * 100 * 100 / 4) / (100 * 100) );
    
  }

  
  @Nested
  class CropInOfRectangle {
    Rectangle base = new Rectangle(100, 100, 100, 100);

    @BeforeEach
    public void setup() {
      base = new Rectangle(100, 100, 100, 100);
    }
    
    @Test
    void noIntersection() throws Exception {
      Rectangle other = new Rectangle(201, 100, 100, 100);
      
      base.cropInRectangle(other);
      
      assertThat(base).isEqualTo(new Rectangle(100, 100, 100, 100));
    }

    @Test
    void tooShort() throws Exception {
      Rectangle other = new Rectangle(100, 100, 50, 50);
      
      base.cropInRectangle(other);
      
      assertThat(base).isEqualTo(new Rectangle(100, 100, 100, 100));
    }
    
    @Test
    void englobingBottom() throws Exception {
      Rectangle other = new Rectangle(50, 50, 200, 100);
      
      base.cropInRectangle(other);
      
      assertThat(base).isEqualTo(new Rectangle(100, 150, 100, 50));
    }
    
    @Test
    void englobingTop() throws Exception {
      Rectangle other = new Rectangle(50, 150, 200, 100);
      
      base.cropInRectangle(other);
      
      assertThat(base).isEqualTo(new Rectangle(100, 100, 100, 50));
    }
    
    @Test
    void englobingLeft() throws Exception {
      Rectangle other = new Rectangle(50, 50, 100, 200);
      
      base.cropInRectangle(other);
      
      assertThat(base).isEqualTo(new Rectangle(150, 100, 50, 100));
    }

    @Test
    void englobingRight() throws Exception {
      Rectangle other = new Rectangle(150, 50, 100, 200);
      
      base.cropInRectangle(other);
      
      assertThat(base).isEqualTo(new Rectangle(100, 100, 50, 100));
    }
  }
  
  
  @Nested
  class CropOutOfCircle {
    
    @Test
    void rectCompletlyInside() throws Exception {
      Rectangle rect = new Rectangle(100, 100, 200, 200);
      rect.cropOutsideCircle(0, 0, 2000);
      assertThat(rect).isEqualTo(new Rectangle(100,100,200,200));
    }
    
    @Test
    void rectCompletlyOutside() throws Exception {
      Rectangle rect = new Rectangle(100, 100, 200, 200);
      rect.cropOutsideCircle(150, 150, 50);
      assertThat(rect).isEqualTo(new Rectangle(100,100,100,100));
    }
    
    @Test
    void rectAtTheBottom() throws Exception {
      Rectangle rect = new Rectangle(1123, 8854, 5035, 1146);
      rect.cropOutsideCircle(2502, 7405, 2000);
      assertThat(rect).isEqualTo(new Rectangle(1123, 8854, 2757, 551));
    }

    @Test
    void rectAtTheBottom2() throws Exception {
      Rectangle rect = new Rectangle(-400, -600, 800, 800);
      rect.cropOutsideCircle(0, 400, 300);
      assertThat(rect).isEqualTo(new Rectangle(-223, 100, 446, 100));
    }
    
    @Test
    void rectAtTheTop() throws Exception {
      Rectangle rect = new Rectangle(-400, -600, 800, 800);
      rect.cropOutsideCircle(0, -800, 300);
      assertThat(rect).isEqualTo(new Rectangle(-223, -600, 446, 100));
    }
    @Test
    void rectAtTheTopButSmaller() throws Exception {
      Rectangle rect = new Rectangle(-400, -600, 800, 800);
      rect.cropOutsideCircle(0, -800, 600);
      assertThat(rect).isEqualTo(new Rectangle(-400, -600, 800, 400));
    }
    
    @Test
    void circleOnASide() throws Exception {
      Rectangle rect = new Rectangle(-400, -600, 800, 800);
      rect.cropOutsideCircle(700, -400, 500);
      assertThat(rect).isEqualTo(new Rectangle(200, -600, 200, 600));
    }
  }
  
  
  
  
  @Test
  @Disabled("buggé")
  void intersections() throws Exception {
    // entre les 2 ys
    Rectangle rect = new Rectangle(0, 0, 100, 100);
    rect.cropInsideCircle(-50, 50, 100);
    assertThat(rect).isEqualTo(rect(50, 0, 50, 100));
    
    // trop loin, pas d'intersection
    rect = new Rectangle(0, 0, 100, 100);
    rect.cropInsideCircle(-150, 50, 100);
    assertThat(rect).isEqualTo(rect(0, 0, 100, 100));
    
    
    // au dessus
    rect = new Rectangle(0, 0, 1000, 100);
    rect.cropInsideCircle(-50, -50, 200);
    assertThat(rect).isEqualTo(rect(143, 0, 857, 100));
    
    // au dessous
    rect = new Rectangle(0, 0, 1000, 100);
    rect.cropInsideCircle(-50, 150, 200);
    assertThat(rect).isEqualTo(rect(143, 0, 857, 100));

  }
  
  @Test
  @Disabled("buggé")
  void cropCircleRight() throws Exception {
    Rectangle rect;

    // de l'autre coté (en x)
    rect = new Rectangle(0, 0, 100, 100);
    rect.cropInsideCircle(150, 50, 100);
    assertThat(rect).isEqualTo(rect(0, 0, 50, 100));

    
    rect = new Rectangle(0, 0, 1000, 100);
    rect.cropInsideCircle(1050, -50, 200);
    assertThat(rect).isEqualTo(rect(0, 0, 857, 100));
    

    rect = new Rectangle(0, 0, 1000, 100);
    rect.cropInsideCircle(1050, 150, 200);
    assertThat(rect).isEqualTo(rect(0, 0, 857, 100));
  }

  
  Rectangle rect(int x, int y, int width, int height) {
    return new Rectangle(x,y,width, height);
  }
}
