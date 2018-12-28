
package CG_editor;

import java.util.ArrayList;
import java.util.List;


public class Solver {
   static float EQN_EPS = 1e-9f;

   public static boolean isZero(float x) {
       return ((x) > -EQN_EPS && (x) < EQN_EPS);
   }

   public static List<Float> solve2(List<Float> c) {
       float p = c.get(1) / (2 * c.get(2));
       float q = c.get(0) / c.get(2);

       float D = p * p - q;

       if (isZero(D)) {
           List<Float> ans = new ArrayList();
           ans.add(-p);
           return ans;
       }
       else if (D < 0) {
           return null;
       }
       else {
           List<Float> ans = new ArrayList();
           float sqrt_D = (float)Math.sqrt(D);
           ans.add(sqrt_D - p);
           ans.add(-sqrt_D - p);
           return ans;
       }
   }

   public static List<Float> solve3(List<Float> c) {


       float A = c.get(2) / c.get(3);
       float B = c.get(1) / c.get(3);
       float C = c.get(0) / c.get(3);

       float sq_A = A * A;
       float p = 1.0f / 3 * (- 1.0f / 3 * sq_A + B);
       float q = 1.0f / 2 * (2.0f / 27 * A * sq_A - 1.0f / 3 * A * B + C);

       float cb_p = p * p * p;
       float D = q * q + cb_p;

       List<Float> s = new ArrayList();

       if (isZero(D)) {
           if (isZero(q)) {
               s.add((float)0);
           }
           else  {
               float u = (float)Math.cbrt(-q);
               s.add(2 * u);
               s.add(-u);
           }
       }
       else if (D < 0) /* Casus irreducibilis: three real solutions */ {
            float phi = (float)(1.0f / 3 * Math.acos(-q / Math.sqrt(-cb_p)));
            float t = 2 * (float)Math.sqrt(-p);
            s = new ArrayList();
            s.add(t * (float)Math.cos(phi));
            s.add(-t * (float)Math.cos(phi + Math.PI / 3));
            s.add(-t * (float)Math.cos(phi - Math.PI / 3));
       }
       else /* one real solution */ {
           float sqrt_D = (float)Math.sqrt(D);
           float u = (float)Math.cbrt(sqrt_D - q);
           float v = -(float)Math.cbrt(sqrt_D + q);
           s = new ArrayList();
           s.add(u + v);
       }

       /* resubstitute */

       float sub = 1.0f / 3 * A;

       for (int i = 0; i < s.size(); ++i)
           s.set(i, s.get(i) - sub);

       return s;
   }


   public static List<Float> solve4(List<Float> c) {

       float A = c.get(3) / c.get(4);
       float B = c.get(2) / c.get(4);
       float C = c.get(1) / c.get(4);
       float D = c.get(0) / c.get(4);


       float sq_A = A * A;
       float p = - 3.0f / 8 * sq_A + B;
       float q = 1.0f / 8 * sq_A * A - 1.0f / 2 * A * B + C;
       float r = - 3.0f / 256 * sq_A * sq_A + 1.0f / 16 * sq_A * B - 1.0f / 4 * A * C + D;
       List<Float> s = new ArrayList();

       if (isZero(r)) {

           List<Float> coeffs = new ArrayList();
           coeffs.add(q);
           coeffs.add(p);
           coeffs.add((float)0);
           coeffs.add((float)1);
           s = solve3(coeffs);
           s.add((float)0);
       }
       else {

           List<Float> coeffs = new ArrayList();
           coeffs.add(1.0f / 2 * r * p - 1.0f / 8 * q * q);
           coeffs.add(-r);
           coeffs.add(-1.0f / 2 * p);
           coeffs.add((float)1);

           s = solve3(coeffs);


           float z = s.get(0);


           float u = z * z - r;
           float v = 2 * z - p;

           if (isZero(u))
               u = 0;
           else if (u > 0)
               u = (float)Math.sqrt(u);
           else
               return null;

           if (isZero(v))
               v = 0;
           else if (v > 0)
               v = (float)Math.sqrt(v);
           else
               return null;
           coeffs = new ArrayList();
           coeffs.add(z - u);
           coeffs.add(q < 0 ? -v : v);
           coeffs.add((float)1);


           s = solve2(coeffs);

           coeffs = new ArrayList();
           coeffs.add(z + u);
           coeffs.add(q < 0 ? v : -v);
           coeffs.add((float)1);              
           
           List<Float> ansss = solve2(coeffs);
           if (ansss != null) {
                for(int i = 0; i < ansss.size(); i++) {
                    s.add(ansss.get(i));
                }
           }
       }

       float sub = 1.0f / 4 * A;
       if (s != null) {
            for (int i = 0; i < s.size(); ++i)
                s.set(i, s.get(i) - sub);
       }

       return s;
   }
}
