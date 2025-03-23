#!/usr/bin/env python3

from PIL import Image, ImageDraw;
from math import sin, cos, pi, tau;

with Image.new(mode="RGBA", size=(80,80)) as im:
    draw = ImageDraw.Draw(im);
    mid = (im.size[0]/2, im.size[1]/2);
    r = 15;
    clr = "#891010";
    draw.circle(
            mid,
            radius=r,
            fill=None,
            outline=clr,
            width=2
        );
    draw.point(mid, fill=clr);
    #tick_angles = [tau*x/4 + tau/8 for x in range(0,4)];
    tick_angles = [(2*x + 1)*tau/8 for x in range(0,4)];
    for a in tick_angles:
        x = cos(a);
        y = sin(a);
        coords  = (x * (r-5) + 40, y * (r-5) + 40);
        coords += (x * (r+7) + 40, y * (r+7) + 40);
        print(coords);
        draw.line(coords, fill=clr, width=2)
    im.save("target.png");

with Image.new(mode="RGBA", size=(80,80)) as im:
    draw = ImageDraw.Draw(im);
    r = 15;
    draw.circle(
            (40,40),
            radius=r,
            fill=None,
            outline="#891010",
            width=3
        );
    im.save("other_one.png");
