
public class tp1
{
  public static void main(String[] args)
  {
    int borne = 12;
    int lowThreshold = 2;
    int highThreshold = 1024;
    try
    {
      for (int i = 1; i <= borne; i++)
      {
        MatriceCarree mat1 = new MatriceCarree("mat-" + i + "-a.txt");
        MatriceCarree mat2 = new MatriceCarree("mat-" + i + "-b.txt");
        long currentTime = System.currentTimeMillis();
        mat1.multConventionnel(mat2);
        double timeConv = 0.001*(System.currentTimeMillis() - currentTime);
        System.out.format("Multiplication de matrices dimension=%4d temps conventionnelle       = %8.3f s.\n", 1<<i, timeConv);
        for (int threshold = lowThreshold; threshold <= highThreshold && threshold <= (1<<i); threshold += threshold)
        {
          currentTime = System.currentTimeMillis();
          mat1.multDiv(mat2, threshold);
          double timeDiv = 0.001*(System.currentTimeMillis() - currentTime);
          System.out.format("Multiplication de matrices dimension=%4d temps Strassen (seuil=%4d) = %8.3f s.\n", 1<<i, threshold, timeDiv);
        }
        System.out.println();
      }
    }

    catch (Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();

    }
  }
}
