package com.main.ateam.member.controller;

import java.util.List;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.main.ateam.member.service.MemberService;
import com.main.ateam.vo.MemberVO;

@RestController
public class MemberController {

	@Autowired
	private MemberService memberService;
	
	
	@GetMapping("/memberlist")
	public @ResponseBody String memberList(){
		List<MemberVO> mlist = memberService.getList();
		Gson gson = new Gson();
		String res = gson.toJson(mlist);
		return res;
	}
}