package com.petfinder.dataaccess.ws;

import android.content.Context;

import com.petfinder.R;
import com.petfinder.dataaccess.entity.Pet;
import com.petfinder.util.Util.HttpHelper;
import com.petfinder.util.Util.SharedPreferencesHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PetWs implements Ws<Pet> {

    private final String URL;
    private static final String ENDPOINT = "PetService.svc";

    private static final String INSERT_METHOD = "NewPet";
    private static final String UPDATE_METHOD = "";
    private static final String DELETE_METHOD = "FindPet";
    private static final String FIND_METHOD = "DeletePet";
    private static final String FIND_ALL_METHOD = "FindAllPets";

    private static final String ID = "Id";
    private static final String NAME = "Name";
    private static final String AGE = "Age";
    private static final String DESCRIPTION = "Description";
    private static final String LOST_LAT = "LostLat";
    private static final String LOST_LON = "LostLon";
    private static final String RACE = "Race";
    private static final String SIZE = "Size";
    private static final String IMG_PATH = "ImagePath";
    private static final String IMG_EXTENSION = "ImageExtension";
    private static final String IMG_BASE_64 = "ImageBase64";

    private static final String RESPONSE = "Response";
    private static final String RESPONSE_CODE = "ResponseCode";

    private Context context;

    public PetWs(Context context) {
        this.context = context;
        Object u = SharedPreferencesHelper.getValue(context.getString(R.string.ip_key), context);
        URL = u != null ? u.toString() : null;
    }

    @Override
    public void insert(Pet toInsert, final WsCallback<Object> onResult) {
        final JSONObject obj = new JSONObject();
        try {
            obj.put(NAME, toInsert.getName());
            obj.put(AGE, toInsert.getAge());
            obj.put(DESCRIPTION, toInsert.getDescription());
            obj.put(LOST_LAT, toInsert.getLostLat());
            obj.put(LOST_LON, toInsert.getLostLon());
            obj.put(RACE, toInsert.getRace());
            obj.put(SIZE, toInsert.getSize());
            obj.put(IMG_BASE_64, toInsert.getImageBase64());
            obj.put(IMG_EXTENSION, toInsert.getImageExtension());


        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }

        /*val wrappedOnResult:(res:Any ?) ->Unit = f @ {
            onResult(it)
        }*/

        if (URL != null) {
            HttpHelper.makeRequest(
                    String.format("%s/%s/%s", URL, ENDPOINT, INSERT_METHOD),
                    HttpHelper.POST, obj.toString(),
                    new HttpHelper.OnResult() {
                        @Override
                        public void execute(Object response) {
                            if(onResult != null)
                                onResult.execute(response);
                        }
                    }
            );
        } else {
            if (onResult != null)
                onResult.execute(null);
        }
        //String s = obj.toString();
        //System.out.println(s);
    }

    @Override
    public void update(Pet toUpdate, WsCallback<Boolean> onResult) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public void delete(Object id, WsCallback<Boolean> onResult) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public void find(Object id, WsCallback<Pet> onResult) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public void findAll(final WsCallback<List<Pet>> onResult) {

        if (URL != null) {
            HttpHelper.makeRequest(String.format("%s/%s/%s", URL, ENDPOINT, FIND_ALL_METHOD),
                    HttpHelper.GET, null, new HttpHelper.OnResult() {
                @Override
                public void execute(Object response) {
                    try {
                        JSONArray arr = new JSONObject(response.toString()).getJSONArray(RESPONSE);
                        List<Pet> pets = new ArrayList<>();
                        Pet p;
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);

                            p = new Pet();
                            p.setId(obj.getInt(ID));
                            p.setName(obj.getString(NAME));
                            p.setDescription(obj.getString(DESCRIPTION));
                            p.setLostLat(obj.getDouble(LOST_LAT));
                            p.setLostLon(obj.getDouble(LOST_LON));
                            p.setRace(obj.getString(RACE));
                            p.setSize(obj.getString(SIZE));
                            p.setImagePath(obj.getString(IMG_PATH));

                            pets.add(p);
                        }

                        onResult.execute(pets);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else{
            onResult.execute(null);
        }
    }
}
