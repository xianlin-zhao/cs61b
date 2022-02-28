public class Planet {
    public double xxPos;
    public double yyPos;
    public double xxVel;
    public double yyVel;
    public double mass;
    public String imgFileName;

    static final double G = 6.67e-11;

    public Planet(double xP, double yP, double xV, double yV, double m, String img){
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = m;
        imgFileName = img;
    }

    public Planet(Planet p){
        xxPos = p.xxPos;
        yyPos = p.yyPos;
        xxVel = p.xxVel;
        yyVel = p.yyVel;
        mass = p.mass;
        imgFileName = p.imgFileName;
    }

    public double calcDistance(Planet p){
        double ans = (xxPos - p.xxPos) * (xxPos - p.xxPos) + (yyPos - p.yyPos) * (yyPos - p.yyPos);
        ans = Math.sqrt(ans);
        return ans;
    }

    public double calcForceExertedBy(Planet p){
        double dist = this.calcDistance(p);
        double ans = G * mass * p.mass / (dist * dist);
        return ans;
    }

    public double calcForceExertedByX(Planet p){
        double dx = p.xxPos - xxPos;
        double F = this.calcForceExertedBy(p);
        double dis = this.calcDistance(p);
        return F * dx / dis;
    }

    public double calcForceExertedByY(Planet p){
        double dy = p.yyPos - yyPos;
        double F = this.calcForceExertedBy(p);
        double dis = this.calcDistance(p);
        return F * dy / dis;
    }

    public double calcNetForceExertedByX(Planet[] ps){
        double Fx = 0.0;
        for(int i = 0; i < ps.length; i++){
            if(this.equals(ps[i])){
                continue;
            }
            Fx += this.calcForceExertedByX(ps[i]);
        }
        return Fx;
    }

    public double calcNetForceExertedByY(Planet[] ps){
        double Fy = 0.0;
        for(int i = 0; i < ps.length; i++){
            if(this.equals(ps[i])){
                continue;
            }
            Fy += this.calcForceExertedByY(ps[i]);
        }
        return Fy;
    }

    public void update(double dt, double fX, double fY){
        double aX = fX / mass;
        double aY = fY / mass;
        xxVel += dt * aX;
        yyVel += dt * aY;
        xxPos += dt * xxVel;
        yyPos += dt * yyVel;
    }

    public void draw(){
        StdDraw.picture(xxPos, yyPos, "images/" + imgFileName);
    }
}
