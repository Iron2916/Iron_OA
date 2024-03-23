package com.iron.auth.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iron.model.system.SysMenu;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author iron
 * @since 2024-03-19
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> findByUserId(Long userId);
}
