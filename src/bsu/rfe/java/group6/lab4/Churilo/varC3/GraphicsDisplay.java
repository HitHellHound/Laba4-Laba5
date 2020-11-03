package bsu.rfe.java.group6.lab4.Churilo.varC3;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GraphicsDisplay extends JPanel {
    private Double[][] graphics1Data;

    private boolean showAxis = true;
    private boolean showMarkers = true;

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    private double scale;

    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;

    private Font axisFont;

    public GraphicsDisplay() {
        setBackground(Color.WHITE);

        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] {8, 2, 8, 2, 8, 2, 2, 2, 2, 2, 2, 2}, 0.0f);
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);

        axisFont = new Font("Serif", Font.BOLD, 36);
    }

    public void showGraphics(Double[][] graphicsData){
        this.graphics1Data = graphicsData;
        repaint();
    }

    public void setShowAxis(boolean showAxis){
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers){
        this.showMarkers = showMarkers;
        repaint();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        if (graphics1Data == null || graphics1Data.length == 0) return;

        minX = graphics1Data[0][0];
        maxX = minX;
        minY = graphics1Data[0][1];
        maxY = minY;
        for (int i = 1; i < graphics1Data.length; i++){
            if (graphics1Data[i][0] < minX)
                minX = graphics1Data[i][0];
            if (graphics1Data[i][0] > maxX)
                maxX = graphics1Data[i][0];
            if (graphics1Data[i][1] < minY)
                minY = graphics1Data[i][1];
            if (graphics1Data[i][1] > maxY)
                maxY = graphics1Data[i][1];
        }

        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);
        scale = Math.min(scaleX, scaleY);

        if (scale == scaleX){
            double yIncrement = (getSize().getHeight()  / scale - (maxY - minY)) / 2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale == scaleY){
            double xIncrement = (getSize().getWidth()/scale - (maxX - minX))/2;
            maxX += xIncrement;
            minX -= xIncrement;
        }

        Graphics2D canvas = (Graphics2D)g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if (showAxis) paintAxis(canvas);
        paintGraphics(canvas);
        if (showMarkers) paintMarkers(canvas);

        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    private void paintGraphics(Graphics2D canvas){
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.RED);

        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < graphics1Data.length; i++){
            Point2D.Double point = xyToPoint(graphics1Data[i][0], graphics1Data[i][1]);
            if (i > 0)
                graphics.lineTo(point.getX(), point.getY());
            else
                graphics.moveTo(point.getX(),point.getY());
        }

        canvas.draw(graphics);
    }

    private void paintMarkers(Graphics2D canvas){
        canvas.setStroke(markerStroke);
        boolean isBlack;
        canvas.setPaint(Color.BLACK);

        for (Double[] point: graphics1Data){
            GeneralPath marker = new GeneralPath();
            Point2D.Double center = xyToPoint(point[0], point[1]);

            String f = point[1].toString();
            int i = 0;
            int sum = 0;
            while (f.charAt(i) != '.' && f.charAt(i) != ','){
                sum += f.charAt(i) - '0';
                i++;
            }
            if (sum < 10)
                canvas.setColor(Color.BLACK);
            else
                canvas.setColor(Color.RED);

            marker.moveTo(center.getX(), center.getY());
            marker.lineTo(center.getX(), center.getY() - 5);
            marker.lineTo(center.getX() + 5, center.getY() - 5);

            marker.moveTo(center.getX(), center.getY());
            marker.lineTo(center.getX() + 5, center.getY());
            marker.lineTo(center.getX() + 5, center.getY() + 5);

            marker.moveTo(center.getX(), center.getY());
            marker.lineTo(center.getX(), center.getY() + 5);
            marker.lineTo(center.getX() - 5, center.getY() + 5);

            marker.moveTo(center.getX(), center.getY());
            marker.lineTo(center.getX() - 5, center.getY());
            marker.lineTo(center.getX() - 5, center.getY() - 5);

            canvas.draw(marker);
        }
    }

    private void paintAxis(Graphics2D canvas){
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);
        FontRenderContext context = canvas.getFontRenderContext();

        //Oy
        if (minX <= 0.0 && 0.0 <= maxX){
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));

            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());
            arrow.closePath();

            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            canvas.drawString("y", (float)(labelPos.getX() - bounds.getWidth() - 10), (float)(labelPos.getY() - bounds.getY()));
        }

        //Ox
        if (minY <= 0.0 && 0.0 <= maxY){
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));

            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);
            arrow.closePath();

            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            canvas.drawString("x", (float)(labelPos.getX() - bounds.getWidth() - 10), (float)(labelPos.getY() - bounds.getY()));
        }
    }

    protected Point2D.Double xyToPoint(double x, double y){
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX * scale, deltaY * scale);
    }

    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY){
        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() - deltaY);
        return dest;
    }
}
