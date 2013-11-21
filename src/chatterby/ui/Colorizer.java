package chatterby.ui;

import java.awt.Color;

/**
 * Picks a color based on some data.
 * 
 * @author scoleman
 * @version 1.0.0
 * @since 1.0.0
 */
public class Colorizer
{
    private static Color[] COLORS = new Color[] {
            Color.BLUE,
            Color.CYAN.darker(),
            Color.GREEN.darker(),
            Color.MAGENTA,
            Color.ORANGE,
            // Color.PINK, /* The pink is hideous. */
            Color.RED,
            Color.YELLOW.darker()
    };

    public static Color colorize(String s)
    {
        int codePointSum = 0;
        for (int i = 0; i < s.length(); i++)
            codePointSum ^= s.codePointAt(i);

        return Colorizer.COLORS[codePointSum % Colorizer.COLORS.length];
    }
}
