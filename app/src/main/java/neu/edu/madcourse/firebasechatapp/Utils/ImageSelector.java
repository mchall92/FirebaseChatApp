package neu.edu.madcourse.firebasechatapp.Utils;

import android.widget.ImageView;

import neu.edu.madcourse.firebasechatapp.R;

public class ImageSelector {
    public ImageSelector() {
    }

    public static void select(ImageView imageView, String s) {
        int sum = 0;
        for (char c : s.toCharArray()) {
            sum += c;
        }
        switch (sum % 35) {
            case 0:
                imageView.setImageResource(R.drawable.aa);
                break;
            case 1:
                imageView.setImageResource(R.drawable.ab);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ac);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ad);
                break;
            case 4:
                imageView.setImageResource(R.drawable.ae);
                break;
            case 5:
                imageView.setImageResource(R.drawable.af);
                break;
            case 6:
                imageView.setImageResource(R.drawable.ag);
                break;
            case 7:
                imageView.setImageResource(R.drawable.ai);
                break;
            case 8:
                imageView.setImageResource(R.drawable.aj);
                break;
            case 9:
                imageView.setImageResource(R.drawable.b);
                break;
            case 10:
                imageView.setImageResource(R.drawable.c);
                break;
            case 11:
                imageView.setImageResource(R.drawable.d);
                break;
            case 12:
                imageView.setImageResource(R.drawable.e);
                break;
            case 13:
                imageView.setImageResource(R.drawable.f);
                break;
            case 14:
                imageView.setImageResource(R.drawable.g);
                break;
            case 15:
                imageView.setImageResource(R.drawable.h);
                break;
            case 16:
                imageView.setImageResource(R.drawable.i);
                break;
            case 17:
                imageView.setImageResource(R.drawable.j);
                break;
            case 18:
                imageView.setImageResource(R.drawable.k);
                break;
            case 19:
                imageView.setImageResource(R.drawable.l);
                break;
            case 20:
                imageView.setImageResource(R.drawable.m);
                break;
            case 21:
                imageView.setImageResource(R.drawable.n);
                break;
            case 22:
                imageView.setImageResource(R.drawable.o);
                break;
            case 23:
                imageView.setImageResource(R.drawable.p);
                break;
            case 24:
                imageView.setImageResource(R.drawable.q);
                break;
            case 25:
                imageView.setImageResource(R.drawable.r);
                break;
            case 26:
                imageView.setImageResource(R.drawable.s);
                break;
            case 27:
                imageView.setImageResource(R.drawable.t);
                break;
            case 28:
                imageView.setImageResource(R.drawable.u);
                break;
            case 29:
                imageView.setImageResource(R.drawable.v);
                break;
            case 30:
                imageView.setImageResource(R.drawable.w);
                break;
            case 31:
                imageView.setImageResource(R.drawable.x);
                break;
            case 32:
                imageView.setImageResource(R.drawable.y);
                break;
            case 33:
                imageView.setImageResource(R.drawable.z);
                break;
            case 34:
                imageView.setImageResource(R.drawable.ah);
                break;
            default:
                imageView.setImageResource(R.drawable.ab);
                break;
        }
    }
}
