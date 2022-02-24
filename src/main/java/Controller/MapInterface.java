package Controller;

public interface MapInterface<T> {

        public T[][] getMap ();

        public void setMap (T[][] map);

        public T getTile (int x, int y);

        public void setTile (int x, int y, T tile);

        public void createMap(int width, int height, float scaling);

        public void insertElement(int x1, int y1, int x2, int y2, T tile);

        public void insertElement(Vector2D[] points, T tile);

}
