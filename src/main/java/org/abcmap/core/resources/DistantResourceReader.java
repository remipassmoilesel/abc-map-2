package org.abcmap.core.resources;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.MapManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by remipassmoilesel on 23/01/17.
 */
public class DistantResourceReader {

    private static final CustomLogger logger = LogManager.getLogger(MapManager.class);

    /**
     * Parse a JSON string as a resource index
     *
     * @param jsonStr
     * @return
     * @throws IOException
     */
    public static ArrayList<DistantResource> parseResourceIndex(String jsonStr, String baseUrl) throws IOException {

        ArrayList<DistantResource> result = new ArrayList<>();

        JSONArray contentArray;
        try {
            JSONObject mainObject = new JSONObject(jsonStr);
            contentArray = mainObject.getJSONArray(DistantResourceConstants.content);
        } catch (Exception e) {
            throw new IOException("Error while parsing JSON", e);
        }

        for (Object o : contentArray) {

            try {
                // test if object
                if (o instanceof JSONObject) {
                    JSONObject res = (JSONObject) o;

                    // all resources should have this parameters
                    String name = res.getString(DistantResourceConstants.name);

                    // all resources can have these parameters
                    String description = "";
                    if (res.keySet().contains(DistantResourceConstants.description_fr)) {
                        description = (String) res.get(DistantResourceConstants.description_fr);
                    }

                    String type = res.getString(DistantResourceConstants.type);

                    //
                    // resource is a wms resource
                    //
                    if (DistantResourceConstants.wms.equals(type)) {

                        String url = res.getString(DistantResourceConstants.url);
                        WmsResource wmsRes = new WmsResource(name, url);
                        wmsRes.setDescription(description);

                        result.add(wmsRes);

                    }

                    //
                    // resource is a shape file
                    //
                    else if (DistantResourceConstants.shapefile.equals(type)) {

                        String path = res.getString(DistantResourceConstants.path);

                        String size = "-";
                        if (res.keySet().contains(DistantResourceConstants.size)) {
                            size = (String) res.get(DistantResourceConstants.size);
                        }

                        ShapefileResource shapefileRes = new ShapefileResource(name, baseUrl, path);
                        shapefileRes.setDescription(description);
                        shapefileRes.setSize(size);
                        shapefileRes.setBaseUrl(baseUrl);

                        result.add(shapefileRes);
                    } else {
                        logger.warning("Unknown JSON object type: " + res.toString());
                    }
                }
            } catch (Exception e) {
                logger.error(new IOException("Error while parsing JSON", e));
            }
        }

        return result;

    }


}
