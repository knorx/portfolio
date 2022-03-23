package name.abuchen.portfolio.ui.views.dashboard.heatmap;

import java.util.function.DoubleFunction;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import name.abuchen.portfolio.ui.Messages;
import name.abuchen.portfolio.ui.util.Colors;

enum ColorSchema
{
    GREEN_YELLOW_RED(Messages.LabelGreenYellowRed), //
    RED_YELLOW_GREEN(Messages.LabelRedYellowGreen), //
    GREEN_WHITE_RED(Messages.LabelGreenWhiteRed);

    // use a darker green to improve readability for the green-white-red schema
    private static final RGB GREEN = new RGB(104, 229, 23);

    private String label;

    private ColorSchema(String label)
    {
        this.label = label;
    }

    @Override
    public String toString()
    {
        return label;
    }
    
    private float getGYRHue(double performance, double max) {
        // convert to 0 = -max -> 1 = +max

        double p = performance;
        p = Math.max(-max, p);
        p = Math.min(max, p);
        p = (p + max) * (1 / (2 * max));

        // 0 = red, 60 = yellow, 120 = green
        float hue = (float) p * 120f;
        
        return hue;
    }
    
    private float getRYGHue(double performance, double max) {
        return 120f - getGYRHue(performance, max);
    }

    /* package */ DoubleFunction<Color> buildColorFunction(ResourceManager resourceManager)
    {
        switch (this)
        {
            case GREEN_YELLOW_RED:
                return performance -> {
                    return resourceManager.createColor(new RGB(getGYRHue(performance, 0.07f), 0.9f, 1f));
                };
            case RED_YELLOW_GREEN:
                return performance -> {
                    return resourceManager.createColor(new RGB(getRYGHue(performance, 0.01f), 0.9f, 1f));
                };
            case GREEN_WHITE_RED:
                return performance -> {

                    final double max = 0.07f;

                    double p = performance;
                    p = Math.min(max, Math.abs(p));
                    p = p / max;

                    RGB color = performance > 0f ? GREEN : Colors.RED.getRGB();
                    return resourceManager.createColor(
                                    Colors.interpolate(Colors.theme().defaultBackground().getRGB(), color, (float) p));
                };
            default:
                throw new IllegalArgumentException();
        }
    }

}
