package CG_editor;

import java.util.ArrayList;
import java.util.List;

public class RayTracing {
    private static int NearestObjectIndex(ArrayList<Float> intersections) {
	int index_of_minimum_value = -1;
	if (intersections.isEmpty()) {
            return -1;
	}
	else {
            float min = -1;
            for (int i = 0; i < intersections.size(); i++) {
                if (intersections.get(i) > 0.1) {
                    min = intersections.get(i);
                    break;
                }
            }
            if (min > 0) {
                for (int index = 0; index < intersections.size(); index++) {
                    if (intersections.get(index) > 0.1 && intersections.get(index) <= min) { // 0.1 чтобы исключить себя
                        min = intersections.get(index);
                        index_of_minimum_value = index;
                    }
                }

                return index_of_minimum_value;
            }
            else {
                return -1;
            }
	}
    }
    
    
    private static Vector4 RefractVector(Vector4 I, Vector4 N, float ior) 
    {
        float eta = 1.0f/ior;
        N = N.Negative();
        float cosi = -I.Dot3(N);
        if (cosi < 0){
            cosi *= -1;
            N = N.Negative();
            eta = 1.f / eta;
        }
        float k = 1 - eta * eta * (1 - cosi * cosi); 
        if (k < 0)
            return null;
        return I.Mul(eta).Add(N.Mul(eta * cosi - (float)Math.sqrt(k))).Normalized();
    }
    
    private static Vector4 ReflectVector(Vector4 I, Vector4 N) 
    {       
        return I.Add(N.Mul(N.Dot3(I.Negative())).Mul(2)).Normalized();
    }
    
    
    private static ColorCG getColor(Vector4 intersection_position, Vector4 intersecting_ray_direction, List<PrimitiveObject> scene_objects, int index_of_nearest_object, int index_of_first_object, List<Source> light_sources, float accuracy, float ambientlight, int recur_deep) {
        ColorCG nearest_object_color = scene_objects.get(index_of_nearest_object).getColor(intersection_position);
	Vector4 nearest_object_normal = scene_objects.get(index_of_nearest_object).getNormalAt(intersection_position).Normalized();

	
	ColorCG final_color = nearest_object_color.ColorMul(ambientlight);

        if (recur_deep > 500)
            System.out.println(recur_deep);

	// тени
	for (int light_index = 0; light_index < light_sources.size(); light_index++) {
            Vector4 light_direction = light_sources.get(light_index).getLightPosition().Sub(intersection_position).Normalized();
            
            float cosine_angle = nearest_object_normal.Dot3(light_direction);

            if (cosine_angle > 0) {
                boolean shadowed = false;

                Vector4 distance_to_light = light_sources.get(light_index).getLightPosition().Sub(intersection_position);
                float distance_to_light_magnitude = distance_to_light.Length3();
                Vector4 distance_to_light_norm = distance_to_light.Normalized();
                Ray shadow_ray = new Ray(intersection_position, distance_to_light_norm);
                List<Float> secondary_intersections = new ArrayList();

                for (int object_index = 0; object_index < scene_objects.size() && shadowed == false; object_index++) {
                    secondary_intersections.add(scene_objects.get(object_index).findIntersection(shadow_ray));
                }

                for (int c = 0; c < secondary_intersections.size(); c++) {
                    if (secondary_intersections.get(c) > 0.01f) {
                        if (secondary_intersections.get(c) <= distance_to_light_magnitude) {
                            
                            shadowed = true;
                        }
                        break;
                    }
                }
                if (shadowed == false) {
                    float intensive_cos = cosine_angle * light_sources.get(light_index).getLightIntensive()/(nearest_object_normal.Length3() * light_direction.Length3());
                    final_color = final_color.ColorAdd(nearest_object_color.ColorMul(light_sources.get(light_index).getLightColor()).ColorMul( intensive_cos));
                    // Находим отраженный луч 
                    if (nearest_object_color.specular > 0) {    
                        Vector4 scalar1 = nearest_object_normal.Mul(cosine_angle).Mul(2);
                        Vector4 reflection_direction = scalar1.Sub(light_direction).Normalized();
                        float specular = reflection_direction.Dot3(intersecting_ray_direction.Negative());
                        if (specular > 0.1) {
                            float specular_object = nearest_object_color.specular * 100;
                            specular = (float)Math.pow(specular, specular_object );
                            final_color = final_color.ColorAdd(light_sources.get(light_index).getLightColor().ColorMul(0.5f * specular * light_sources.get(light_index).getLightIntensive()));
                        }

                    }
                }
            }
	}
        
        // отражение
	if (nearest_object_color.getColorSpecial() > 0 && nearest_object_color.getColorSpecial() <= 1) { 
            Vector4 reflection_direction = ReflectVector(intersecting_ray_direction, nearest_object_normal);
            Ray reflection_ray = new Ray(intersection_position, reflection_direction);
            ArrayList<Float> reflection_intersections = new ArrayList();

            for (int reflection_index = 0; reflection_index < scene_objects.size(); reflection_index++) {
                reflection_intersections.add(scene_objects.get(reflection_index).findIntersection(reflection_ray));
            }
            
            
            int index_of_nearest_object_with_reflection = NearestObjectIndex(reflection_intersections);
            if (index_of_nearest_object_with_reflection != -1 && recur_deep < 10) {
                if (reflection_intersections.get(index_of_nearest_object_with_reflection) > 0.01) {
                    Vector4 reflection_intersection_position = intersection_position.Add(reflection_direction.Mul(reflection_intersections.get(index_of_nearest_object_with_reflection)));
                    Vector4 reflection_intersection_ray_direction = reflection_direction;

                    ColorCG reflection_intersection_color = getColor(reflection_intersection_position, reflection_intersection_ray_direction, scene_objects, index_of_nearest_object_with_reflection, index_of_nearest_object_with_reflection, light_sources, 0.0001f, ambientlight, ++recur_deep);

                    final_color = final_color.ColorMul(1 - final_color.getColorSpecial()).ColorAdd(reflection_intersection_color.ColorMul(nearest_object_color.getColorSpecial()));
                }
            } 
	}
        
        
        // преломление
        if (nearest_object_color.getColorRefr() > 0) {
            ;
            Vector4 refraction_direction = RefractVector(intersecting_ray_direction, nearest_object_normal, nearest_object_color.getColorRefr());
            if (refraction_direction != null) {
                
                Ray refraction_ray = new Ray(intersection_position, refraction_direction);
                ArrayList<Float> refraction_intersections = new ArrayList();
                

                for (int refraction_index = 0; refraction_index < scene_objects.size(); refraction_index++) {
                    if ( refraction_index != index_of_first_object) { 
                        refraction_intersections.add(scene_objects.get(refraction_index).findIntersection(refraction_ray));
                        
                    } else {
                        refraction_intersections.add(-1.f);
                    }
                        
                }
                int index_of_nearest_object_with_refraction = NearestObjectIndex(refraction_intersections);
                if (index_of_nearest_object_with_refraction != -1 && recur_deep < 10) {
                    if (refraction_intersections.get(index_of_nearest_object_with_refraction) > 0.1) {
                        
                        Vector4 refraction_intersection_position = intersection_position.Add(refraction_direction.Mul(refraction_intersections.get(index_of_nearest_object_with_refraction)));
                        Vector4 refraction_intersection_ray_direction = refraction_direction;

                        ColorCG refraction_intersection_color = getColor(refraction_intersection_position, refraction_intersection_ray_direction, scene_objects, index_of_nearest_object_with_refraction, index_of_nearest_object_with_refraction, light_sources, 0.0001f, ambientlight, ++recur_deep);

                        final_color = final_color.ColorMul(1 - nearest_object_color.opacity).ColorAdd(refraction_intersection_color.ColorMul(nearest_object_color.opacity));
                    }
                }
            }

        }
	return final_color.Limit();
    }
    
   
    
    private static float[] norm_coords(int x, int y, float increment, int width, int height, float aspectratio){
        float xamnt, yamnt;
        //float tanHalfFOV = (float)Math.tan(70.f / 2);
        //return new float[] {x, y};
        if (width > height) {
            xamnt = ((x + increment)/width)*aspectratio - (((width-height)/(float)height)/2);
            yamnt = (y + increment)/height;
        }
        else if (height > width) {
            // the imager is taller than it is wide
            xamnt = (x + increment)/ width;
            yamnt = ((y + increment)/height)/aspectratio - (((height - width)/(float)width)/2);
        }
        else {
            // the image is square
            xamnt = (x + increment)/width;
            yamnt = ((y) + increment)/height;
        }
        
        return new float[] {xamnt, yamnt};
    }
    class TracingThread implements Runnable {
        RenderSceneTriangle target;
        int width;
        int height;
        Camera camera;
        List<PrimitiveObject> scene_objects;
        List<Source> light_sources;
        float ambientlight;
        int aliasing_koef, begin, end;
        public TracingThread(RenderSceneTriangle target_v, int width_v, int height_v, 
                                         Camera camera_v, List<PrimitiveObject> scene_objects_v, List<Source> light_sources_v, 
                                         float ambientlight_v, int aadepth_v, int begin_v, int end_v) {
            target = target_v;
            width = width_v;
            height = height_v;
            camera = camera_v;
            scene_objects = scene_objects_v;
            light_sources = light_sources_v;
            ambientlight = ambientlight_v;
            aliasing_koef = aadepth_v;
            begin = begin_v;
            end = end_v;
        }

       public void run() {
           Vector4 campos = camera.getCameraPosition();
            Vector4 camdir = camera.getCameraDirection();
            Vector4 camright = camera.getCameraRight();
            Vector4 camdown = camera.getCameraDown();

            float aspectratio = (float)width/(float)height;
            int aliasing_index;
            float xamnt, yamnt;
            float accuracy = 0.001f;

            for (int x = begin; x < end; x++) {
                for (int y = 0; y < height; y++) {
                    float tempRed[] = new float[aliasing_koef*aliasing_koef];
                    float tempGreen[] = new float[aliasing_koef*aliasing_koef];
                    float tempBlue[] = new float[aliasing_koef*aliasing_koef];

                    for (int aax = 0; aax < aliasing_koef; aax++) {
                        //for (int aay = 0; aay < aliasing_koef; aay++) {
                        int aay = 0;
                            aliasing_index = aay*aliasing_koef + aax;

                            if (aliasing_koef == 1) {
                                float result[] = norm_coords(x, y, 0.5f, width, height, aspectratio);
                                xamnt = result[0];
                                yamnt = result[1];
                            } else {
                                float result[] = norm_coords(x, y, (float)aax/((float)aliasing_koef), width, height, aspectratio);
                                xamnt = result[0];
                                yamnt = result[1];
                            }
                            Vector4 cam_ray_origin = campos;
                            Vector4 cam_ray_direction = camdir.Add(camright.Mul(xamnt - 0.5f).Add(camdown.Mul(yamnt - 0.5f))).Normalized();
                            Ray cam_ray = new Ray(cam_ray_origin, cam_ray_direction);
                            ArrayList<Float> intersections = new ArrayList();
                            for (int index = 0; index < scene_objects.size(); index++) {
                                intersections.add(scene_objects.get(index).findIntersection(cam_ray));
                            }
                            int index_of_nearest_object = NearestObjectIndex(intersections);
                            if (index_of_nearest_object == -1) {
                                tempRed[aliasing_index] = 0;
                                tempGreen[aliasing_index] = 0;
                                tempBlue[aliasing_index] = 0;
                            } else {
                                if (intersections.get(index_of_nearest_object) > 0.00001) {

                                    Vector4 intersection_position = cam_ray_origin.Add(cam_ray_direction.Mul(intersections.get(index_of_nearest_object)));
                                    Vector4 intersecting_ray_direction = cam_ray_direction;
                                    int recur_deep = 0;

                                    ColorCG intersection_color = getColor(intersection_position, intersecting_ray_direction, scene_objects, index_of_nearest_object, index_of_nearest_object, light_sources, accuracy, ambientlight, recur_deep);

                                    tempRed[aliasing_index] = intersection_color.getColorRed();
                                    tempGreen[aliasing_index] = intersection_color.getColorGreen();
                                    tempBlue[aliasing_index] = intersection_color.getColorBlue();


                                }
                            }
                        //}
                    }  
                    float totalRed = 0;
                    float totalGreen = 0;
                    float totalBlue = 0;

                    for (int iRed = 0; iRed < aliasing_koef; iRed++) {
                            totalRed = totalRed + tempRed[iRed];
                    }
                    for (int iGreen = 0; iGreen < aliasing_koef; iGreen++) {
                            totalGreen = totalGreen + tempGreen[iGreen];
                    }
                    for (int iBlue = 0; iBlue < aliasing_koef; iBlue++) {
                            totalBlue = totalBlue + tempBlue[iBlue];
                    }

                    double avgRed = totalRed/(aliasing_koef);
                    double avgGreen = totalGreen/(aliasing_koef);
                    double avgBlue = totalBlue/(aliasing_koef);

                    target.DrawPixel(x, y, (byte)0xFF,
                                            (byte)((int)(avgBlue * 255) & 0xFF),
                                            (byte)((int)(avgGreen * 255) & 0xFF),
                                            (byte)((int)(avgRed * 255) & 0xFF));
                }
            }  
        }
    }
    
    public void render_ray_tracing (RenderSceneTriangle target, int width, int height, 
                                    Camera camera, List<PrimitiveObject> scene_objects, List<Source> light_sources, 
                                    float ambientlight, int aadepth) throws InterruptedException {
        
        int thread_number = 16;
        Thread[] t_arr = new Thread[thread_number];
        
        int num_of_parts = width / thread_number;
        int num_of_parts_remainder = width % thread_number;
        
        for (int i = 0; i < thread_number - 1; ++i) {
            int begin = i * num_of_parts;
            int end = begin + num_of_parts;
            t_arr[i] = new Thread(new TracingThread(target, width, height, camera, scene_objects, light_sources, ambientlight, aadepth, begin, end));
            t_arr[i].start();
        }
        int begin = (thread_number - 1) * num_of_parts;
        int end = begin + num_of_parts + num_of_parts_remainder;
        t_arr[thread_number - 1] = new Thread(new TracingThread(target, width, height, camera, scene_objects, light_sources, ambientlight, aadepth, begin, end));
        t_arr[thread_number - 1].start();
        //long start = System.nanoTime();

	for (int i = 0; i < thread_number; ++i) {
            t_arr[i].join();
        }	
        //long finish = System.nanoTime();
        //System.out.println("Время работы = " + (finish - start)); //767174622    935248932  329834388
        // с - 851408253  без - 3022528243
    }                                                                                                   
}


