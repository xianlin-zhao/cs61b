import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {

    public Rasterer() {
        // YOUR CODE HERE
    }

    private boolean outOfBound(double ullon, double ullat, double lrlon, double lrlat) {
        if (ullon > lrlon || ullat < lrlat) {
            return true;
        }
        if (lrlon <= MapServer.ROOT_ULLON || lrlat >= MapServer.ROOT_ULLAT) {
            return true;
        }
        if (ullon >= MapServer.ROOT_LRLON || ullat <= MapServer.ROOT_LRLAT) {
            return true;
        }
        return false;
    }

    private int getDepth(double ullon, double lrlon, double w) {
        double wish = (lrlon - ullon) / w;
        double ori = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / MapServer.TILE_SIZE;
        int depth = 0;
        while (depth < 7) {
            if (ori <= wish) {
                break;
            }
            ori = ori / 2.0;
            depth++;
        }
        return depth;
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        // System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        double ullon = params.get("ullon");
        double lrlon = params.get("lrlon");
        double ullat = params.get("ullat");
        double lrlat = params.get("lrlat");
        double w = params.get("w");
        double h = params.get("h");

        String[][] render_grid;
        double raster_ul_lon = 0.0;
        double raster_ul_lat = 0.0;
        double raster_lr_lon = 0.0;
        double raster_lr_lat = 0.0;
        int depth = 0;
        boolean query_success = true;

        if (outOfBound(ullon, ullat, lrlon, lrlat)) {
            query_success = false;
            render_grid = new String[1][1];
        } else {
            depth = getDepth(ullon, lrlon, w);
            double widthPerPic = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / Math.pow(2.0, depth);
            double heightPerPic = (MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT) / Math.pow(2.0, depth);
            int xL = (int) Math.floor((ullon - MapServer.ROOT_ULLON) / widthPerPic);
            int xR = (int) Math.floor((lrlon - MapServer.ROOT_ULLON) / widthPerPic);
            int yL = (int) Math.floor((MapServer.ROOT_ULLAT - ullat) / heightPerPic);
            int yR = (int) Math.floor((MapServer.ROOT_ULLAT - lrlat) / heightPerPic);

            raster_ul_lon = MapServer.ROOT_ULLON + xL * widthPerPic;
            raster_lr_lon = MapServer.ROOT_ULLON + (xR + 1) * widthPerPic;
            raster_ul_lat = MapServer.ROOT_ULLAT - yL * heightPerPic;
            raster_lr_lat = MapServer.ROOT_ULLAT - (yR + 1) * heightPerPic;

            if (ullon < MapServer.ROOT_ULLON) {
                xL = 0;
                raster_ul_lat = MapServer.ROOT_ULLON;
            }
            if (ullat > MapServer.ROOT_ULLAT) {
                yL = 0;
                raster_ul_lat = MapServer.ROOT_ULLAT;
            }
            if (lrlon > MapServer.ROOT_LRLON) {
                xR = (int) Math.pow(2.0, depth) - 1;
                raster_lr_lon = MapServer.ROOT_LRLON;
            }
            if (lrlat < MapServer.ROOT_LRLAT) {
                yR = (int) Math.pow(2.0, depth) - 1;
                raster_lr_lat = MapServer.ROOT_LRLAT;
            }

            render_grid = new String[yR - yL + 1][xR - xL + 1];
            for (int i = yL; i <= yR; i++) {
                for (int j = xL; j <= xR; j++) {
                    render_grid[i - yL][j - xL] = "d" + depth + "_x" + j + "_y" + i + ".png";
                }
            }
        }

        results.put("render_grid", render_grid);
        results.put("raster_ul_lon", raster_ul_lon);
        results.put("raster_ul_lat", raster_ul_lat);
        results.put("raster_lr_lon", raster_lr_lon);
        results.put("raster_lr_lat", raster_lr_lat);
        results.put("depth", depth);
        results.put("query_success", query_success);
        return results;
    }

}
