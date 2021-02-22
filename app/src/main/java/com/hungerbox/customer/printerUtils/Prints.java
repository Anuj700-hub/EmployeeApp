package com.hungerbox.customer.printerUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.OrderSubProduct;
import com.lvrenyang.io.Pos;

import java.io.IOException;
import java.io.InputStream;

public class Prints {

    public static int PrintTicket(Order order, Context ctx, Pos pos, int nPrintWidth, boolean bCutter, boolean bDrawer, boolean bBeeper, int nCount, int nPrintContent, int nCompressMethod) {
        int bPrintResult = -8;

        byte[] status = new byte[1];
        if (pos.POS_RTQueryStatus(status, 3, 1000, 2)) {


            if ((status[0] & 0x08) == 0x08)   //判断切刀是否异常
                return bPrintResult = -2;

            if ((status[0] & 0x40) == 0x40)   //判断打印头是否在正常值范围内
                return bPrintResult = -3;


            if (pos.POS_RTQueryStatus(status, 2, 1000, 2)) {

                if ((status[0] & 0x04) == 0x04)    //判断合盖是否正常
                    return bPrintResult = -6;
                if ((status[0] & 0x20) == 0x20)    //判断是否缺纸
                    return bPrintResult = -5;
                else {
                    for (int i = 0; i < nCount; i++) {
                        if (!pos.GetIO().IsOpened())
                            break;

                        if (nPrintContent >= 1) {
                            pos.POS_Reset();
                            pos.POS_FeedLine();
                            pos.POS_TextOut(" Order Receipt", 3, 16, 1, 1, 0, 0);
                            pos.POS_TextOut("\r\n", 3, 16, 0, 0, 0, 0);
                            pos.POS_TextOut(" -----------------------------\r\n", 3, 0, 0, 0, 0, 0);
                            pos.POS_S_Align(1);
                            pos.POS_TextOut(" Customer Slip", 3, 0, 0, 0, 0, 0);
                            pos.POS_FeedLine();
                            if (order.getKot() > 0) {
                                pos.POS_TextOut(" Kot : " + order.getKot() + "\r\n", 3, 0, 0, 0, 0, 0x08);
                            }
                            pos.POS_S_Align(0);
                            pos.POS_TextOut(" Order No      : " + order.getOrderId() + " \r\n", 3, 0, 0, 0, 0, 0);
                            pos.POS_TextOut(" Order PIN     : " + order.getPin() + "\r\n", 3, 0, 0, 0, 0, 0);

                            pos.POS_TextOut(" Vendor        : " + order.getVendorName() + "\r\n", 3, 0, 0, 0, 0, 0);
                            pos.POS_TextOut(" Order Time    : " + order.getCreatedAtString() + "\r\n", 3, 0, 0, 0, 0, 0);

                            pos.POS_TextOut(" Items Ordered :\r\n", 3, 0, 0, 0, 0, 0x80);

                            for (OrderProduct orderProduct : order.getProducts()) {
                                String productName = "";
                                if (orderProduct.getName().length() < 18) {
                                    productName = padRight(orderProduct.getName(),18);
                                } else {
                                    productName = orderProduct.getName().substring(0, 18);
                                }
                                String str = productName + "   " + orderProduct.quantity + " X " + orderProduct.getPrice();
                                pos.POS_TextOut(" " + str, 3, 0, 0, 0, 0, 0);

                                if (orderProduct.getSubProducts().size() > 0)
                                    for (OrderSubProduct orderSubProduct : orderProduct.getSubProducts()) {
                                        StringBuffer orderOptionText = new StringBuffer("");
                                        if (orderSubProduct.getOrderOptionItems().size() > 0) {
                                            orderOptionText.append(orderSubProduct.getName() + " : ");
                                            for (int j = 0; j < orderSubProduct.getOrderOptionItems().size(); j++) {
                                                orderOptionText.append(orderSubProduct.getOrderOptionItems().get(j).getName());
                                                if (j < orderSubProduct.getOrderOptionItems().size() - 1) {
                                                    orderOptionText.append('\n');
                                                }
                                            }
                                            String optionString = orderOptionText.toString();
                                            pos.POS_S_Align(0);
                                            pos.POS_FeedLine();
                                            pos.POS_TextOut(" " + optionString, 3, 0, 0, 0, 0, 0);
                                        }
                                    }
                                pos.POS_FeedLine();
                            }

                            pos.POS_S_Align(2);
                            pos.POS_TextOut(" Total = " + order.getPrice() + "\n", 3, 0, 0, 0, 0, 0);
                            //S_ALIGN 0->LEFT,1->CENTER,2->RIGHT
                            pos.POS_S_Align(1);
                            pos.POS_EPSON_SetQRCode(order.getPin(), 8, 3);
                            pos.POS_S_Align(0);
                            pos.POS_FeedLine();
                            pos.POS_TextOut(" -----------------------------\r\n", 3, 0, 0, 0, 0, 0);
                            pos.POS_FeedLine();
                            pos.POS_FeedLine();
                            pos.POS_FeedLine();
                            pos.POS_FeedLine();


                        }
                    }

                    if (bCutter && nCount == 1)
                        pos.POS_FullCutPaper();

                    return 0;


//                    if (nCount == 1) {
//                        try {
//                            Thread.currentThread();
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                        if (pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
//                            if ((status[0] & 0x80) == 0x80) {
//
//                                try {
//                                    Thread.currentThread();
//                                    Thread.sleep(1000);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//
//                                if (pos.POS_RTQueryStatus(status, 2, 1000, 2)) {
//                                    if ((status[0] & 0x20) == 0x20)    //打印过程缺纸
//                                        return bPrintResult = -9;
//                                    if ((status[0] & 0x04) == 0x04)    //打印过程打开纸仓盖
//                                        return bPrintResult = -10;
//                                } else {
//                                    return bPrintResult = -11;         //查询失败
//                                }
//
//
//                                if (pos.POS_RTQueryStatus(status, 4, 1000, 2)) {
//                                    if ((status[0] & 0x08) == 0x08) {
//                                        if (pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
//                                            if ((status[0] & 0x80) == 0x80)     //纸将尽且未取纸
//                                                return bPrintResult = 2;
//                                            else
//                                                return bPrintResult = 1;
//                                        }
//                                    } else {
//                                        if (pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
//                                            if ((status[0] & 0x80) == 0x80) {
//                                                return bPrintResult = 3;
//                                            } else
//                                                return bPrintResult = 0;
//                                        } else
//                                            return bPrintResult = -11;
//                                    }
//                                } else {
//                                    return bPrintResult = -11;         //查询失败
//                                }
//
//
//                            } else {
//                                try {
//                                    Thread.currentThread();
//                                    Thread.sleep(1000);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//
//                                if (pos.POS_RTQueryStatus(status, 2, 1000, 2)) {
//                                    if ((status[0] & 0x20) == 0x20) {
//                                        return bPrintResult = -9;                //Out of paper during printing
//                                    }
//
//                                    if ((status[0] & 0x04) == 0x04) {
//                                        return bPrintResult = -10;                //Open the paper compartment cover during printing
//                                    } else {
//                                        return bPrintResult = -1;
//                                    }
//
//
//                                } else {
//                                    return bPrintResult = -11;         //查询失败
//                                }
//                            }
//
//                        } else {
//                            return bPrintResult = -11;
//                        }
//                    }

                }

            } else {
                return bPrintResult = -8;           //查询失败
            }

        } else {
            return bPrintResult = -8;          //查询失败
        }
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }



    public static int CheckPrinter(Pos pos) {
        int bPrintResult = 0;

        byte[] status = new byte[1];
        if (pos.POS_RTQueryStatus(status, 3, 1000, 2)) {


            if ((status[0] & 0x08) == 0x08)   //Determine if the cutter is abnormal
                return bPrintResult = -2;

            if ((status[0] & 0x40) == 0x40)   //Determine if the print head is within the normal range
                return bPrintResult = -3;


            if (pos.POS_RTQueryStatus(status, 2, 1000, 2)) {

                if ((status[0] & 0x04) == 0x04)    //Determine whether the cover is normal
                    return bPrintResult = -6;
                if ((status[0] & 0x20) == 0x20)    //Determine if there is no paper
                    return bPrintResult = -5;
            } else {
                return bPrintResult = -8;          //查询失败

            }
        }

        return bPrintResult;
    }

    public static String ResultCodeToString(int code) {
        switch (code) {
            case 3:
                return "There are unissued small tickets at the paper exit, please pay attention to the small tickets in time";
            case 2:
                return "The paper will run out and there are unsuccessful receipts at the paper exit. Please pay attention to replacing the paper roll and removing the receipts in time";
            case 1:
                return "The paper is running out, please pay attention to replacing the paper roll";
            case 0:
                return " ";
            case -1:
                return "Small ticket is not printed, please check for paper jam";
            case -2:
                return "Printer Cutter is abnormal, please remove it manually";
            case -3:
                return "The print head is overheating, please wait for the printer to cool down";
            case -4:
                return "Printer is offline";
            case -5:
                return "Printer out of paper";
            case -6:
                return "Printer cover open";
            case -7:
                return "Real-time status query failed";
            case -8:
                return "Query status failed, please check if the communication port is connected normally";
            case -9:
                return "Out of paper during printing, please check document integrity";
            case -10:
                return "The cover is open during printing, please reprint";
            case -11:
                return "The connection is interrupted, please confirm whether the printer is connected";
            case -12:
                return "Please take away the printed receipt before printing!";
            case -100:
                return "No Printer Found.";
            default:
                return "unknown mistake";
        }
    }

    /**
     * 从Assets中读取图片
     */
    public static Bitmap getImageFromAssetsFile(Context ctx, String fileName) {
        Bitmap image = null;
        AssetManager am = ctx.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        // load the origial Bitmap
        Bitmap BitmapOrg = bitmap;

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);

        // make a Drawable from Bitmap to allow to set the Bitmap
        // to the ImageView, ImageButton or what ever
        return resizedBitmap;
    }

    public static Bitmap getTestImage1(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, height, paint);

        paint.setColor(Color.BLACK);
        for (int i = 0; i < 8; ++i) {
            for (int x = i; x < width; x += 8) {
                for (int y = i; y < height; y += 8) {
                    canvas.drawPoint(x, y, paint);
                }
            }
        }
        return bitmap;
    }

    public static Bitmap getTestImage2(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, height, paint);

        paint.setColor(Color.BLACK);
        for (int y = 0; y < height; y += 4) {
            for (int x = y % 32; x < width; x += 32) {
                canvas.drawRect(x, y, x + 4, y + 4, paint);
            }
        }
        return bitmap;
    }
}
