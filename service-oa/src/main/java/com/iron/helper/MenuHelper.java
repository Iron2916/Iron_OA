package com.iron.helper;

import com.iron.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {

    public static List<SysMenu> buildTree(List<SysMenu> menuList) {

        List<SysMenu> result = new ArrayList<>();
        for (SysMenu sysMenu : menuList) {

            if (sysMenu.getParentId().intValue() == 0) {

                result.add(dfs(menuList, sysMenu));
            }
        }
        return result;
    }

    private static SysMenu dfs(List<SysMenu> menuList, SysMenu curMenu) {

        Long curId = curMenu.getId();
        curMenu.setChildren(new ArrayList<>());

        for (SysMenu sysMenu : menuList) {

            if (sysMenu.getParentId().intValue() == curId) {

                curMenu.getChildren().add(sysMenu);
                dfs(menuList, sysMenu);
            }
        }

        return curMenu;
    }
}
