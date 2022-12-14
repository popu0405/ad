package com.main.ateam.member.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.main.ateam.member.memberEtc.MailSendModule;
import com.main.ateam.member.memberEtc.memberInfoModule;
import com.main.ateam.member.service.MemberService;
import com.main.ateam.modules.APILoginModule;
import com.main.ateam.modules.Base64Module;
import com.main.ateam.modules.GsonModule;
import com.main.ateam.modules.SftpModule;
import com.main.ateam.modules.UbuntuShellModule;
import com.main.ateam.vo.CovidRecordVO;
import com.main.ateam.vo.DoctorVO;
import com.main.ateam.vo.FileVO;
import com.main.ateam.vo.HospitalVO;
import com.main.ateam.vo.MemberVO;
import com.main.ateam.vo.OAuthToken;
import com.main.ateam.vo.ReserveVO;

@Controller
@RequestMapping("/member")
public class MemberController {
	@Autowired
	private MailSendModule mailSendModule;
	@Autowired
	private memberInfoModule memberInfoModule;
	@Autowired
	private MemberService memberService;
	
	/* 0918 add: ????????? Autowired ?????? */
	@Autowired
	private APILoginModule apiloginmodule;
	@Autowired
	private SftpModule sftpmodule;
	@Autowired
	private Base64Module base64module;
	@Autowired
	private UbuntuShellModule ubuntushellmodule;
	@Autowired
	private GsonModule gsonmodule;
	
	/* 0918 add: ????????? @Values ?????? */
	@Value("${uploadPath}")
	private String uploadPath;
	@Value("${pythonPath}")
	private String pythonPath;
		

	@GetMapping("/memberLoginForm")
	public String MemberLoginForm() {
		return "member/login_form";
	}

	// ?????? ?????????
	@PostMapping("/memberLogin")
	public ModelAndView MemberLogin(HttpSession session, MemberVO vo) {
		ModelAndView mav = new ModelAndView("redirect:/");
		Map<String, String> map = new HashMap<>();
		map.put("id", vo.getId());
		map.put("pwd", vo.getPwd());
		System.out.println("id = " + vo.getId());
		System.out.println("pwd = " + vo.getPwd());
		MemberVO dto = memberService.memberLogin(map);
		if (dto == null) {
			System.out.println("????????? ??????");
		} else {
			System.out.println("????????? ??????");
			session.setAttribute("sessionID", dto.getId());
			session.setAttribute("sessionNUM", dto.getNum());
			session.setAttribute("sessionNAME", dto.getName());
		}
		return mav;
	}
	
	@ResponseBody
	@PostMapping("loginReact")
	public MemberVO loginReact(@RequestBody String id ,String pwd) {
		Map<String, String> map = new HashMap<>();
		char quotes = '"';
		String[] res = id.split(":|"+quotes+"|,");
		for (String e : res) {
			System.out.println(e);
		}
		System.out.println("id: " +res[4]+"pwd : "+res[10]);
		//String resId = res[0].substring(res[0].length(), res[0].length()-1);
	
		map.put("id", res[4]);
		map.put("pwd", res[10]);
	
		return memberService.memberLogin(map);
	}

	// ?????? ??????
	@GetMapping(value = "/memberLogout")
	public String memberLogout(HttpSession session) {
		session.removeAttribute("sessionID");
		session.removeAttribute("sessionNUM");
		System.out.println("??????????????????");
		return "redirect:/";
	}

	@ResponseBody
	@PostMapping(value = "/idchk")
	public String idchk(String id, String pwd) {
		Map<String, String> map = new HashMap<>();
		map.put("id", id);
		map.put("pwd", pwd);

		System.out.println("controller id => " + id);
		System.out.println("controller pwd => " + pwd);
		System.out.println("map =>" + map);
		int cnt = memberService.idchk(map);
		System.out.println("mem cnt => " + cnt);
		String res = Integer.toString(cnt);
		System.out.println("mem res => " + res);
		return res;
	}

	// ????????? ?????????
	@GetMapping(value = "/test")
	public String test2() {
		
		return "mypage/ad_chart1";
	}

	// ?????? ???????????????
	@GetMapping(value = "/memberMypage")
	public String memberMypage(Model m, HttpSession session) {
		int num = 0;
		num = (int) session.getAttribute("sessionNUM");
		MemberVO vo = memberService.memberMyPage(num);
		m.addAttribute("member", vo);
		return "mypage/member_mypage";
	}
	

	// ?????? ??????????????? - ????????????
	@GetMapping(value = "/memberMypage_list")
	public String memberMypage_list(Model m, HttpSession session) {
		int num = 0;
		num = (int) session.getAttribute("sessionNUM");
		System.out.println("sessionNum=>" + num);
		 List<HospitalVO> vo = memberService.memberReserveList(num);
		 m.addAttribute("num",num);
		 m.addAttribute("vo", vo);
		return "mypage/member_mypage_reserveList";
	}
	// ?????? ??????????????? - ???????????? - ?????????
	@ResponseBody
	@GetMapping(value = "/memberMypage_list_Detail")
	public MemberVO memberMypage_listDetail(HttpSession session,int num) {
		int mnum = 0;
		mnum = (int) session.getAttribute("sessionNUM");
		Map<String, Integer> map = new HashMap<>();
		map.put("num", mnum);
		map.put("reservNum", num);
		System.out.println("num = "+  mnum);
		System.out.println("reservNum = "+  num);
		MemberVO vo = memberService.memberReserveDetail(map);
		
		return vo;
	}

	// ???????????? ?????????
	@GetMapping(value = "/updateMypageForm")
	public String updateMypage(Model m, HttpSession session) {
		int num = 0;
		num = (int) session.getAttribute("sessionNUM");
		MemberVO vo = memberService.memberMyPage(num);
		m.addAttribute("member", vo);
		return "mypage/updateMypage";
	}

	@RequestMapping(value = "/updateMypage" ,method = RequestMethod.POST)
	public String fileupLoad(FileVO v, HttpServletRequest request, MemberVO vo, HttpSession session) {
		int num = 0;
		num = (int) session.getAttribute("sessionNUM");
		String img_path = "imgfile";
		System.out.println("img_path :" + img_path);
		String r_path = request.getRealPath("/");
		System.out.println("r_path :" + r_path);
		String oriFn = v.getFileOriName().getOriginalFilename();
		System.out.println("oriFn : " + oriFn);
		StringBuffer path = new StringBuffer();
		path.append(r_path).append(img_path).append("\\");
		path.append(oriFn);
		vo.setNum(num);
		vo.setProfimg(oriFn);
		File f = new File(path.toString());
		System.out.println("?????? ??????" + f.getPath());
		try {
			v.getFileOriName().transferTo(f);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		memberService.memberUpdate(vo);
		
		return "redirect:memberMypage";
	}

	@ResponseBody
	@GetMapping("/memberlist")
	public List<MemberVO> memberList() {
		List<MemberVO> mlist = memberService.memberList();
		return mlist;
	}

	@GetMapping(value = "/joinForm")
	public ModelAndView joinform() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("member/memberForm");
		return mav;
	}

	@PostMapping(value = "/addMember")
	public String addMember(MemberVO mvo, HttpServletRequest request) {
		System.out.println("???????????? : " + mvo.getSsn());
		System.out.println("id : " + mvo.getId());
		System.out.println("email : " + mvo.getEmail());
		System.out.println("???????????? : " + mvo.getTel());

		int age = memberInfoModule.getAge(mvo.getSsn());
		String gender = memberInfoModule.getGender(mvo.getSsn());
		mvo.setAge(age);
		mvo.setGender(gender);
		// request??? ????????? ???????????? ????????? ????????? ??????
		String img_path = "resources\\imgfile";
		String r_path = request.getRealPath("/");
		System.out.println("r_path : " + r_path);
		String oriFn = mvo.getMfile().getOriginalFilename();
		// ???????????? ????????? ??? contentType??????
		long size = mvo.getMfile().getSize();
		String contentType = mvo.getMfile().getContentType();
		System.out.println("?????? ?????? : " + size);
		System.out.println("?????? ?????? : " + contentType);
		System.out.println("oriFn : " + oriFn);
		StringBuffer path = new StringBuffer();
		path.append(r_path).append(img_path).append("\\");
		path.append(oriFn);
		System.out.println("Fullpath : " + path);
		// ????????????(???????????? ????????? ??????) File ????????? ??????
		File f = new File(path.toString());
		try {
			mvo.getMfile().transferTo(f);
			mvo.setProfimg(oriFn);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}

		memberService.addMemberService(mvo);
		return "redirect:/main";
	}
	
	@CrossOrigin
	@ResponseBody
	@PostMapping(value = "/addMemberReact")
	public String addMemberReact(@RequestParam MemberVO mvo, HttpServletRequest request) {
		System.out.println("???????????? : " + mvo.getSsn());
		System.out.println("id : " + mvo.getId());
		System.out.println("email : " + mvo.getEmail());
		System.out.println("???????????? : " + mvo.getTel());

		int age = memberInfoModule.getAge(mvo.getSsn());
		String gender = memberInfoModule.getGender(mvo.getSsn());
		mvo.setAge(age);
		mvo.setGender(gender);
		// request??? ????????? ???????????? ????????? ????????? ??????
		String img_path = "resources\\imgfile";
		String r_path = request.getRealPath("/");
		System.out.println("r_path : " + r_path);
		String oriFn = mvo.getMfile().getOriginalFilename();
		// ???????????? ????????? ??? contentType??????
		long size = mvo.getMfile().getSize();
		String contentType = mvo.getMfile().getContentType();
		System.out.println("?????? ?????? : " + size);
		System.out.println("?????? ?????? : " + contentType);
		System.out.println("oriFn : " + oriFn);
		StringBuffer path = new StringBuffer();
		path.append(r_path).append(img_path).append("\\");
		path.append(oriFn);
		System.out.println("Fullpath : " + path);
		// ????????????(???????????? ????????? ??????) File ????????? ??????
		File f = new File(path.toString());
		try {
			mvo.getMfile().transferTo(f);
			mvo.setProfimg(oriFn);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}

		memberService.addMemberService(mvo);
		return "redirect:/main";
	}
	
	
	@GetMapping(value = "/idcheck")
	public ModelAndView idCheck(@RequestParam("id") String id) {
		ModelAndView mav = new ModelAndView("member/member/idcheck");
		Map<String, String> map = new HashMap<String, String>();
		map.put("id",id);
		int cnt = memberService.idCheckService(map);
		System.out.println("???????????? id"+id);
		System.out.println("???????????? cnt=>"+cnt);
		mav.addObject("cnt", cnt);
		return mav;
	}

	@RequestMapping(value = "/mailCheck", method = RequestMethod.GET)
	@ResponseBody // ????????? ????????????, url??? ?????? String??? ?????? ??????
	public String mailCheck(String email) {
		System.out.println("From Ajax Email : " + email);
		return mailSendModule.joinEmail(email);
	}
	
	/*-----??????????????????-----*/
	@GetMapping(value = "/auth/kakao/callback")
	public ModelAndView kakaoCallback(HttpSession session, String code) throws JsonMappingException, JsonProcessingException {
		OAuthToken oauthToken = apiloginmodule.getAPIToken("KAKAO", code);
		MemberVO mdto = apiloginmodule.getAPIMember("KAKAO", oauthToken);
		
		ModelAndView mav = new ModelAndView("redirect:/main");
		System.out.println("KAKAOMEMBER : "+mdto.getId());
		int flag = memberService.kakaoFlag(mdto.getId());
		if(flag == 0){
			mav.setViewName("member/kakao_addinfo");
			mav.addObject("kakaoData",mdto);
			return mav;
		}
		session.setAttribute("sessionID", mdto.getId());
		//session.setAttribute("sessionNUM", mdto.getNum()); //???????????? ??????
		session.setAttribute("sessionNAME", mdto.getName());
		return mav;
	}
	/*-----??????????????????-----*/
	@RequestMapping(value = "/auth/naver/callback")
	public ModelAndView naverCallback(HttpSession session, @RequestParam String code, @RequestParam String state) throws JsonMappingException, JsonProcessingException{
		OAuthToken oauthToken = apiloginmodule.getAPIToken("NAVER", code);
		MemberVO mdto = apiloginmodule.getAPIMember("NAVER", oauthToken);
		ModelAndView mav = new ModelAndView("redirect:/main");
		int flag = memberService.kakaoFlag(mdto.getId());
		if(flag == 0){
			memberService.kakaoSignup(mdto);
		}
		session.setAttribute("sessionID", mdto.getId());
		session.setAttribute("sessionNAME", mdto.getName());
		return mav;
	}
	/*-----?????????????????????-----*/
	@PostMapping(value = "kakaoLogin")
	public String kakaoLogin(HttpSession session, MemberVO dtov) {
		dtov.setAge(apiloginmodule.getAge(dtov.getSsn()));
		dtov.setGender(apiloginmodule.getGender(dtov.getSsn()));
		dtov.setAddr("-");
		System.out.println("????????? ?????????:"+dtov.getId());
		System.out.println("????????? ??????: "+dtov.getAge());
		System.out.println("????????? ??????: "+dtov.getGender());
		memberService.kakaoSignup(dtov);
		session.setAttribute("sessionID", dtov.getId());
		session.setAttribute("sessionNAME", dtov.getName());
		return "redirect:/main";
	}
	
	

	
	/* ????????? ?????? ???????????? ????????? */
	@RequestMapping(value = "/COVIDcheck")
	public String covidCheck() {
		return "member/covidRecording";
	}
	/* ?????????????????? ????????? ??????????????? ??? JSON ?????? */
	@PostMapping(value = "/COVIDUpload", produces = "application/json;charset=utf-8")
	public String COVIDUpload(Model model, CovidRecordVO vo, HttpServletRequest request, HttpSession session) throws Exception {
		
		String userID = session.getAttribute("sessionID").toString();
		
		String filename = userID + ".wav";
		String filepath = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/upload\\audio").toString();
		
		String jsonname = userID + ".json";
		String jsonpath = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/upload\\json").toString();
		MemberVO uservo = memberService.userdetail(userID);

		System.out.println("JAVAPATH ??????!=>"+filepath);
		
		//Base64????????? ???????????? ??? ?????? ??????
		base64module.base64ToMultipart(vo.getBase64str(), filename, filepath);
		
		//.wav Upload
		ubuntushellmodule.upload(uploadPath, filepath+"\\"+filename);			

		//{userID}.json Config
		vo.setAge(uservo.getAge());
		vo.setGender(uservo.getGender());
		vo.setUserid(userID);
		vo.setBase64str(filename);
		gsonmodule.saveGsonFile(vo, jsonpath+"\\"+jsonname);
		
		//JsonFile Upload
		ubuntushellmodule.upload(uploadPath, jsonpath+"\\"+jsonname);
		
		//check_covid19.py Model ??????
		//System.out.println("python "+pythonPath+"check_covid19.py");
		ubuntushellmodule.command("python "+pythonPath+"check_covid19.py "+userID);
		
		return "member/covidResult"; 
	}
	
	@RequestMapping("/COVIDResult")
	public String covidresult() {
		return "member/covidResult";
	}
	
	@RequestMapping("/miruchart")
	public String miruchart() {
		return "mypage/miruchart1";
		
	}
	
}