package Tenzinn.Systems.Dungeon;

import java.util.List;

public class WorldConfig {

    public SizeRange size;
    public Content   content;

    public static class SizeRange {
        public SizeDimension min;
        public SizeDimension max;
    }

    public static class SizeDimension {
        public int x, y, z;
    }

    public static class Content {
        public List<String> grounds;
        public List<String> walls;
        public List<String> accents;
        public List<String> decoratives;
        public List<String> roofs;
        public List<String> roots;
        public List<String> plants;
        public List<String> rubbles;
        public List<String> lanterns_floor;
        public List<String> pillar_bases;
        public List<String> pillar_middles;
    }
}
