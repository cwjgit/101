package com.leyou.item.service;

import com.leyou.item.entity.TbSpecGroup;
import com.leyou.item.entity.TbSpecParam;
import com.leyou.pojo.DTO.SpecGroupDTO;
import com.leyou.pojo.DTO.SpecParamDTO;

import java.util.List;

/**
 * @创建人 cwj
 * @创建时间 2019/8/31  10:33
 * @描述
 */
public interface SpecService {
    List<SpecGroupDTO> findSpecGroupById(Long cid);

    void saveSpecGroup(TbSpecGroup tbSpecGroup);

    List<SpecParamDTO> findSpecParamByParam(Long gid, Long cid, Boolean searching);

    void updateSpecGroup(TbSpecGroup tbSpecGroup);

    void deleteSpecGroup(Long id);

    void saveSpecParam(SpecParamDTO specParamDTO);

    void deleteSpecParam(Long id);

    void updateSpecParam(SpecParamDTO specParamDTO);

    List<SpecGroupDTO> findSpecOfCid(Long cid);
}
