package gamelogic.datacarriers;

public record Sound(double angle, double loudness) {
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() == this.getClass()) {
            Sound temp = (Sound) other;
            return temp.angle() == this.angle() && temp.loudness() == this.loudness();
        }
        return false;
    }

    public int compareTo(Sound other) {
        if (other == null) return 1;
        else if (loudness >= other.loudness) return 1;
        else return 0;
    }
}
