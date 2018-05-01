package com.petfinder.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.petfinder.R;
import com.petfinder.dataaccess.entity.Pet;
import com.petfinder.dataaccess.ws.PetWs;
import com.petfinder.dataaccess.ws.Ws;
import com.petfinder.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private static final String IMAGE_KEY = "imagePath";
    private static final String NAME_KEY = "name";
    private static final String RACE_KEY = "race";
    private static final String SIZE_KEY = "size";

    private ListView petListView;
    //private String[] adapterKeys = {"imagePath", "name", "race", "size"};
    private List<Map<String, Object>> pets = new ArrayList<>();

    private PetWs petWs;

    private View layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_dashboard, container, false);

        petListView = layout.findViewById(R.id.lvPet);

        petWs = new PetWs(getContext());

        /*for(int i = 0; i < 100; i++){
            Map<String, Object> map = new HashMap<>();
            map.put("name", i);
            map.put("race", i * 100);
            map.put("size", i * 200);
            pets.add(map);
        }*/

        //if(pets.size() <= 0) {

        try {
            petWs.findAll(new Ws.WsCallback<List<Pet>>() {
                @Override
                public void execute(List<Pet> response) {

                    if (response == null) {// || response.size() <= 0
                        Util.showAlert("Un error ha ocurrido, asegurese de haber porpocionado una IP y puerto correctos en ajustes", getContext());
                        return;
                    }

                    pets.clear();

                    for (Pet p : response) {
                        Map<String, Object> map = new HashMap<>();
                        map.put(NAME_KEY, p.getName());
                        map.put(IMAGE_KEY, p.getImagePath());
                        map.put(RACE_KEY, p.getRace());
                        map.put(SIZE_KEY, p.getSize());
                        pets.add(map);
                    }

                    petListView.setAdapter(new PetAdapter(getContext(), pets, R.layout.item_pet));
                }
            });
        } catch(RuntimeException ex){
            Util.showAlert(ex.getMessage() + "\nProbablemente al IP esta mal formada", getContext());
        }
        //}

        return layout;
    }

    //I ASSUMED RIGHT. from AND to ARRAYS CANT BE NULL NPE WILL BE THROWN, AND GET VIEW GETS ITS COUNT FROM THE ITEMS PARAM
    private class PetAdapter extends SimpleAdapter {

        private ImageView petImageView;
        private TextView nameTextView;
        private TextView raceTextView;
        private TextView sizeTextView;

        private List<Map<String, Object>> petList;

        PetAdapter(Context context, List<Map<String, Object>> petList, int layout) {
            super(context, petList, layout, new String[]{}, new int[]{});
            this.petList = petList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View row = super.getView(position, convertView, parent);

            petImageView = row.findViewById(R.id.imgPet);
            nameTextView = row.findViewById(R.id.tvName);
            raceTextView = row.findViewById(R.id.tvRace);
            sizeTextView = row.findViewById(R.id.tvSize);

            nameTextView.setText(petList.get(position).get(NAME_KEY).toString());
            raceTextView.setText(petList.get(position).get(RACE_KEY).toString());
            sizeTextView.setText(petList.get(position).get(SIZE_KEY).toString());
            String path = petList.get(position).get(IMAGE_KEY).toString();
            if(path != null && !path.isEmpty())
                Picasso.get().load(path).into(petImageView);
            else
                petImageView.setImageResource(R.drawable.common_google_signin_btn_icon_light);

            return row;
        }
    }

}
