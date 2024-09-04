package me.hcfalerts.practice.profile.category;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.profile.category.data.CategoryEditorData;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Getter @Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Category {

    @Getter public static final List<Category> categories = Lists.newArrayList();
    @Getter public static final Set<CategoryEditorData> categoryEditor = Sets.newConcurrentHashSet();

    public final String name;
    public String displayName;
    public int elo;

    public static void init() {
        if (HCFPractice.get().getMainConfig().getBoolean("ELO.CATEGORY_ENABLE")) {
            for (String s : HCFPractice.get().getMainConfig().getStringList("ELO.CATEGORIES")) {
                try {
                    String[] split = s.split(":");
                    categories.add(new Category(split[0], split[2], Integer.parseInt(split[1])));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid category: " + s);
                }
            }
            System.out.println("[HCFPractice] Divisions successfully loaded");
            categories.sort((o1, o2) -> o2.elo - o1.elo);
            Collections.reverse(categories);
        }
    }

    public static void save() {
        BasicConfigurationFile file = HCFPractice.get().getMainConfig();
        List<String> list = Lists.newArrayList();
        for (Category c : categories) {
            list.add(c.getName() + ":" + c.getElo() + ":" + c.getDisplayName());
        }
        file.getConfiguration().set("ELO.CATEGORIES", list);
        try {
            file.getConfiguration().save(file.getFile());
        } catch (IOException e) {
            throw new IllegalArgumentException("Error trying save a " + file.getName());
        }
    }

    public static Category getByName(String name) {
        for (Category c : categories) {
            if (c.name.equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public static Category getByElo(int elo) {
        /*Collections.reverse(list);*/
        Category select = null;
        for (Category c : categories) {
            if (elo >= c.getElo()) {
                select = c;
            }
        }

        return select;
    }
}
