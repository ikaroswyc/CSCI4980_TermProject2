package util;

import org.junit.Test;

public class TestUtilAspectGen {
   UtilAspectGen aspGen = new UtilAspectGen();
   
   @Test
   public void testFindAnnotation() {
      System.out.println("[DBG] Start...");
      aspGen.findAnnotation();
      System.out.println("[DBG] Done.");
   }
}
