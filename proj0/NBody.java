public class NBody {
    public static double readRadius(String fileName){
        In in = new In(fileName);
        int num = in.readInt();
        double radius = in.readDouble();
        return radius;
    }

    public static Planet[] readPlanets(String fileName){
        In in = new In(fileName);
        int num = in.readInt();
        double radius = in.readDouble();
        Planet[] ans = new Planet[num];
        for(int i = 0; i < num; i++){
            double xxPos = in.readDouble();
            double yyPos = in.readDouble();
            double xxVel = in.readDouble();
            double yyVel = in.readDouble();
            double mass =  in.readDouble();
            String imgFileName = in.readString();
            ans[i] = new Planet(xxPos, yyPos, xxVel, yyVel, mass, imgFileName);
        }
        return ans;
    }

    public static void main(String[] args) {
        double T = Double.parseDouble(args[0]);
        double dt = Double.parseDouble(args[1]);
        String filename = args[2];
        double radius = readRadius(filename);
        Planet[] ps = readPlanets(filename);

        StdDraw.setScale(-radius, radius);
        StdDraw.enableDoubleBuffering();
        double t = 0;
        while(t < T){
            double[] xForces = new double[ps.length];
            double[] yForces = new double[ps.length];
            for(int i = 0; i < ps.length; i++){
                xForces[i] = ps[i].calcNetForceExertedByX(ps);
                yForces[i] = ps[i].calcNetForceExertedByY(ps);
            }
            for(int i = 0; i < ps.length; i++){
                ps[i].update(dt, xForces[i], yForces[i]);
            }
            StdDraw.clear();
            StdDraw.picture(0, 0, "images/starfield.jpg");
            for(int i = 0; i < ps.length; i++){
                ps[i].draw();
            }
            StdDraw.show();
            StdDraw.pause(10);
            t += dt;
        }
        StdOut.printf("%d\n", ps.length);
        StdOut.printf("%.2e\n", radius);
        for (int i = 0; i < ps.length; i++) {
            StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                    ps[i].xxPos, ps[i].yyPos, ps[i].xxVel,
                    ps[i].yyVel, ps[i].mass, ps[i].imgFileName);
        }
    }
}
