public class partitionCut {

    public double  sigmaX ;
    public double  sigmaY ;

    public partitionCut(double sigmaX, double sigmaY) {

        this.sigmaY = sigmaY;
        this.sigmaX = sigmaX;
    }

    @Override
    public String toString() {
        return "partitionCut{" +
                "cutValueX=" + sigmaX +
                ", cutValueY=" + sigmaY +
                '}';
    }
}
