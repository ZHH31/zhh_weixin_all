package com.example.weixin.menu.service.impl;

import java.util.List;

import com.example.commons.service.WeixinProxy;
import com.example.weixin.menu.domain.Menu;
import com.example.weixin.menu.domain.SelfMenu;
import com.example.weixin.menu.repository.SelfMenuRepository;
import com.example.weixin.menu.service.SelfMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class SelfMenuServiceImpl implements SelfMenuService {

	private static final Logger LOG = LoggerFactory.getLogger(SelfMenuServiceImpl.class);
	@Autowired
	private SelfMenuRepository menuRepository;

	@Autowired
	private WeixinProxy weixinProxy;

	@Override
	public SelfMenu getMenu() {
		List<SelfMenu> all = menuRepository.findAll();
		if (all.isEmpty()) {
			return new SelfMenu();
		}
		return all.get(0);
	}

	@Override
	public void saveMenu(SelfMenu selfMenu) {
		selfMenu.getSubMenus().forEach(b1 -> {
			if (!b1.getSubMenus().isEmpty()) {
				b1.setAppId(null);
				b1.setKey(null);
				b1.setMediaId(null);
				b1.setPagePath(null);
				b1.setType(null);
				b1.setUrl(null);
			}
		});

		this.menuRepository.deleteAll();
		this.menuRepository.save(selfMenu);

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode buttonNode = mapper.createObjectNode();
		ArrayNode buttonArrayNode = mapper.createArrayNode();
		buttonNode.set("button", buttonArrayNode);

		selfMenu.getSubMenus().forEach(b1 -> {

			ObjectNode menu = mapper.createObjectNode();
			menu.put("name", b1.getName());
			buttonArrayNode.add(menu);
			if (b1.getSubMenus().isEmpty()) {
				setValues(menu, b1);
			} else {
				ArrayNode subButtons = mapper.createArrayNode();
				menu.set("sub_button", subButtons);
				b1.getSubMenus().forEach(b2 -> {
					ObjectNode subMenu = mapper.createObjectNode();
					subMenu.put("name", b2.getName());
					subButtons.add(subMenu);
					setValues(subMenu, b2);
				});
			}
		});

		try {
			String json = mapper.writeValueAsString(buttonNode);

			this.weixinProxy.saveMenu(json);
		} catch (JsonProcessingException e) {
			LOG.error("保存菜单出现问题：" + e.getLocalizedMessage(), e);
		}
	}

	private void setValues(ObjectNode subMenu, Menu m) {
		if (!StringUtils.isEmpty(m.getAppId())) {
			subMenu.put("appid", m.getAppId());
		}

		if ((m.getType().equals("location_select")//地理定位
				|| m.getType().equals("pic_weixin")// 手机相册
				|| m.getType().equals("pic_photo_or_album")// 手机相册或拍照
				|| m.getType().equals("pic_sysphoto")// 拍照
				|| m.getType().equals("scancode_push")// 扫码
				|| m.getType().equals("scancode_waitmsg")// 扫码
		) //
				&& !StringUtils.isEmpty(m.getKey())) {
			subMenu.put("key", m.getKey());
		} else {
			m.setKey(null);
		}
		if (!StringUtils.isEmpty(m.getPagePath())) {
			subMenu.put("pagepath", m.getPagePath());
		}
		if (!StringUtils.isEmpty(m.getMediaId())) {
			subMenu.put("media_id", m.getMediaId());
		}
		if (!StringUtils.isEmpty(m.getUrl())) {
			subMenu.put("url", m.getUrl());
		}
		if (!StringUtils.isEmpty(m.getType())) {
			subMenu.put("type", m.getType());
		}
	}
}
