import React from "react";
import { Link } from "react-router-dom";
import DoctorDetailImg from "../img/doctor_detail.png";
const DoctorDetail = (props) => {
  console.log(props.doctor);

  return (
    <div className="d-flex flex-column align-items-center p">
      <div className="text-center">
        <img src={DoctorDetailImg} className="detailImg" alt="..." />
      </div>
      <div className="card detailCard border-0 p-2">
        <div className="card-body ">
          <div className="detailStatus">
            <strong>현재 진료가능</strong>
          </div>
          <h5 className="card-title">
            <strong> {props.doctor.dname} 의사</strong>
          </h5>
          <span className="detail-card-text">
            {props.doctor.hospitalVO[0].hname}
          </span>
          <span className="detail-card-text">
            　리뷰 : 1,045 <i className="bi bi-chevron-double-right"></i>
          </span>
          <div className="mt-2 d-flex flex-start">
            <button className="btn btn-primary me-1 disabled" type="button">
              {props.doctor.dmajor}
            </button>
          </div>
        </div>
        <div className="card-body border-top border-bottom ">
          <p>
            <strong>진료시간　</strong>
            {props.doctor.hospitalVO[0].otime} ~{" "}
            {props.doctor.hospitalVO[0].ctime}
          </p>
          <p>
            <strong>점심시간　</strong>13:00 ~ 14:00
          </p>
        </div>
        <div className="card-body border-bottom">
          <p>
            <strong>진료과목</strong> {props.doctor.dmajor}
          </p>
          <p>
            <strong>약력　　</strong> {props.doctor.dmajor}
          </p>
        </div>
        <div className="card-body border-bottom">
          <h6>
            <strong>의사소개</strong>
          </h6>
          <div className="card-text">
            안녕하세요 {props.doctor.dname} 의사입니다.
            <br /> 친절하고 꼼꼼하게 진료하겠습니다. <br />
            *다이어트 포함 비급여 항목 확인해주세요
          </div>
        </div>
        <div className="card-body border-bottom">
          <h6>
            <strong>병원위치</strong>
          </h6>
          <div className="card-text">{props.doctor.hospitalVO[0].hloc}</div>
          <div className="detailMap"></div>
        </div>
      </div>
      <div className="card detailCard border-0 p-2 mt-5">
        <div className="card-body ">
          <h5>리뷰 점수 1000 개</h5>
        </div>
      </div>
      <Link to={"/reservation/" + props.doctor.dnum}>
        <button className="btn bg-primary btn-lg text-white mt-3 revBtn">
          진료 예약하기
        </button>
      </Link>
    </div>
  );
};

export default DoctorDetail;
