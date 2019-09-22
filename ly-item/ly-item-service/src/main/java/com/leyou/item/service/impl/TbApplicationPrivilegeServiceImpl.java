package com.leyou.item.service.impl;

import com.leyou.item.entity.TbApplicationPrivilege;
import com.leyou.item.mapper.TbApplicationPrivilegeMapper;
import com.leyou.item.service.TbApplicationPrivilegeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务中间表，记录服务id以及服务能访问的目标服务的id 服务实现类
 * </p>
 *
 * @author HM
 * @since 2019-08-27
 */
@Service
public class TbApplicationPrivilegeServiceImpl extends ServiceImpl<TbApplicationPrivilegeMapper, TbApplicationPrivilege> implements TbApplicationPrivilegeService {

}
