import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ContrastiveLearningDemo {

    static final int SIZE = 64;

    public static void main(String[] args) throws Exception {

        BufferedImage dogImg = ImageIO.read(new File("dog.png"));
        BufferedImage catAImg = ImageIO.read(new File("cat_a.png"));
        BufferedImage catBImg = ImageIO.read(new File("cat_b.png"));

        double[] dogVec = imageToVector(dogImg);
        double[] catAVec = imageToVector(catAImg);
        double[] catBVec = imageToVector(catBImg);

        double dogCatB = l2Distance(dogVec, catBVec);
        double catACatB = l2Distance(catAVec, catBVec);

        double C = 50.0;

        double lossDogCatB = contrastiveLoss(C, dogCatB);
        double lossCatACatB = contrastiveLoss(C, catACatB);

        System.out.println("===== 圖片相似度比較 =====");
        System.out.println("dog vs cat_b Distance = " + dogCatB);
        System.out.println("cat_a vs cat_b Distance = " + catACatB);

        System.out.println();
        System.out.println("===== Contrastive Loss =====");
        System.out.println("dog vs cat_b Loss L = " + lossDogCatB);
        System.out.println("cat_a vs cat_b Loss L = " + lossCatACatB);

        System.out.println();
        System.out.println("===== 判斷結果 =====");

        if (dogCatB < catACatB) {
            System.out.println("dog 比較像 cat_b");
        } else if (catACatB < dogCatB) {
            System.out.println("cat_a 比較像 cat_b");
        } else {
            System.out.println("dog 和 cat_a 跟 cat_b 的相似度一樣");
        }
    }

    public static double[] imageToVector(BufferedImage img) {

        BufferedImage resized = new BufferedImage(
                SIZE,
                SIZE,
                BufferedImage.TYPE_BYTE_GRAY
        );

        Graphics2D g = resized.createGraphics();
        g.drawImage(img, 0, 0, SIZE, SIZE, null);
        g.dispose();

        double[] vector = new double[SIZE * SIZE];

        int index = 0;

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {

                int pixel = resized.getRGB(x, y);
                int gray = pixel & 0xff;

                // 正規化成 0~1，避免 distance 太大
                vector[index++] = gray / 255.0;
            }
        }

        return vector;
    }

    public static double l2Distance(double[] a, double[] b) {

        double sum = 0;

        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }

        return Math.sqrt(sum);
    }

    public static double contrastiveLoss(double C, double distance) {
        return Math.max(0, C - distance);
    }
}
