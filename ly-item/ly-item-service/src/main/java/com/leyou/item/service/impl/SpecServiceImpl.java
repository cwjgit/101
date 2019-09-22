package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.CollectionUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.entity.TbSpecGroup;
import com.leyou.item.entity.TbSpecParam;
import com.leyou.item.service.SpecService;
import com.leyou.item.service.TbSpecGroupService;
import com.leyou.item.service.TbSpecParamService;
import com.leyou.pojo.DTO.SpecGroupDTO;
import com.leyou.pojo.DTO.SpecParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @创建人 cwj
 * @创建时间 2019/8/31  10:33
 * @描述
 */
@Service
public class SpecServiceImpl implements SpecService {

    @Autowired
    private TbSpecGroupService tbSpecGroupService;
    @Autowired
    private TbSpecParamService tbSpecParamService;

    /**
     * 根据分类id查询规格组信息
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroupDTO> findSpecGroupById(Long cid) {
        QueryWrapper<TbSpecGroup> tbSpecGroupQueryWrapper  = new QueryWrapper<>();
        tbSpecGroupQueryWrapper.lambda().eq(TbSpecGroup::getCid,cid);
        List<TbSpecGroup> tbSpecGroups = tbSpecGroupService.list(tbSpecGroupQueryWrapper);
        return BeanHelper.copyWithCollection(tbSpecGroups,SpecGroupDTO.class);
    }

    /**
     * 新增规格组信息
     * @param tbSpecGroup
     * @return
     */
    @Override
    public void saveSpecGroup(TbSpecGroup tbSpecGroup) {
        boolean save = tbSpecGroupService.save(tbSpecGroup);
        if(!save){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 查询规格参数信息
     * @param gid 分组id
     * @param cid 分类id
     * @param searching 是否搜索
     * @return
     */
    @Override
    public List<SpecParamDTO> findSpecParamByParam(Long gid, Long cid, Boolean searching) {
        QueryWrapper<TbSpecParam> tbSpecParamQueryWrapper = new QueryWrapper<>();
        if(gid != null && gid != 0){
            tbSpecParamQueryWrapper.lambda().eq(TbSpecParam::getGroupId,gid);
        }
        if(cid != null && cid != 0){
            tbSpecParamQueryWrapper.lambda().eq(TbSpecParam::getCid,cid);
        }
        if(searching != null){
            tbSpecParamQueryWrapper.lambda().eq(TbSpecParam::getSearching,searching);
        }
        List<TbSpecParam> tbSpecParams = tbSpecParamService.list(tbSpecParamQueryWrapper);
        if (CollectionUtils.isEmpty(tbSpecParams)) {
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(tbSpecParams,SpecParamDTO.class);
    }

    /**
     * 更新规格分组信息
     * @param tbSpecGroup
     * @return
     */
    @Override
    public void updateSpecGroup(TbSpecGroup tbSpecGroup) {
        boolean b = tbSpecGroupService.updateById(tbSpecGroup);
        if(!b){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }

    /**
     * 删除规格分组信息
     * @param id
     * @return
     */
    @Override
    public void deleteSpecGroup(Long id) {
        QueryWrapper<TbSpecParam> tbSpecParamQueryWrapper  = new QueryWrapper<>();
        tbSpecParamQueryWrapper.lambda().eq(TbSpecParam::getGroupId,id);
        List<TbSpecParam> list = tbSpecParamService.list(tbSpecParamQueryWrapper);
        if(!CollectionUtils.isEmpty(list)){
            tbSpecParamQueryWrapper.lambda().eq(TbSpecParam::getGroupId,id);
            boolean remove = tbSpecParamService.remove(tbSpecParamQueryWrapper);
            if(!remove){
                throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
            }
        }
        boolean b = tbSpecGroupService.removeById(id);
        if(!b){
            throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }
    }

    /**
     * 添加规格属性信息
     * @param specParamDTO
     * @return
     */
    @Override
    public void saveSpecParam(SpecParamDTO specParamDTO) {
        TbSpecParam tbSpecParam = BeanHelper.copyProperties(specParamDTO, TbSpecParam.class);
        tbSpecParam.setIsNumeric(specParamDTO.getNumeric());
        boolean save = tbSpecParamService.save(tbSpecParam);
        if(!save){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 删除规格属性信息
     * @param id
     * @return
     */
    @Override
    public void deleteSpecParam(Long id) {
        boolean remove = tbSpecParamService.removeById(id);
        if(!remove){
            throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }
    }

    /**
     * 更新规格属性信息
     * @param specParamDTO
     * @return
     */
    @Override
    public void updateSpecParam(SpecParamDTO specParamDTO) {
        TbSpecParam tbSpecParam = BeanHelper.copyProperties(specParamDTO, TbSpecParam.class);
        tbSpecParam.setIsNumeric(specParamDTO.getNumeric());
        boolean b = tbSpecParamService.updateById(tbSpecParam);
        if(!b){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }

    /**
     *根据categoryId查询规格参数组和组内参数
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroupDTO> findSpecOfCid(Long cid) {
        QueryWrapper<TbSpecGroup> specGroupDTOQueryWrapper  = new QueryWrapper<>();
        specGroupDTOQueryWrapper.lambda().eq(TbSpecGroup::getCid,cid);
        //查询组集合
        List<TbSpecGroup> tbSpecGroups = tbSpecGroupService.list(specGroupDTOQueryWrapper);
        if(CollectionUtils.isEmpty(tbSpecGroups)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        //转换为DTO
        List<SpecGroupDTO> specGroupDTOS = BeanHelper.copyWithCollection(tbSpecGroups, SpecGroupDTO.class);
        /*//循环组DTO
        for (SpecGroupDTO specGroupDTO : specGroupDTOS) {
            //根据组id和分类id查询对应的规格参数
            QueryWrapper<TbSpecParam> tbSpecParamQueryWrapper = new QueryWrapper<>();
            tbSpecParamQueryWrapper.lambda().eq(TbSpecParam::getCid,cid).eq(TbSpecParam::getGroupId,specGroupDTO.getId());
            List<TbSpecParam> tbSpecParams = tbSpecParamService.list(tbSpecParamQueryWrapper);
            if(CollectionUtils.isEmpty(tbSpecParams)){
                throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
            }
            //转换组参数为DTO赋值给相应的组
            specGroupDTO.setParams(BeanHelper.copyWithCollection(tbSpecParams,SpecParamDTO.class));
        }*/

        //查询此分类下的所有参数
        QueryWrapper<TbSpecParam> tbSpecParamQueryWrapper = new QueryWrapper<>();
        tbSpecParamQueryWrapper.lambda().eq(TbSpecParam::getCid,cid);
        List<TbSpecParam> tbSpecParams = tbSpecParamService.list(tbSpecParamQueryWrapper);
        List<SpecParamDTO> specParamDTOS = BeanHelper.copyWithCollection(tbSpecParams, SpecParamDTO.class);
        //根据组id进行分组 形成map集合
        Map<Long, List<SpecParamDTO>> collect = specParamDTOS.stream().collect(Collectors.groupingBy(SpecParamDTO::getGroupId));
        for (SpecGroupDTO specGroupDTO : specGroupDTOS) {
            specGroupDTO.setParams(collect.get(specGroupDTO.getId()));
        }
        return specGroupDTOS;
    }
}
