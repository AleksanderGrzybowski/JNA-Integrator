package org.kelog.japroj.web;

import com.google.gson.Gson;
import spark.ResponseTransformer;


public class JsonTransformer implements ResponseTransformer {

	// safe to use as 'static'
	private static Gson gson = new Gson();

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }

}
