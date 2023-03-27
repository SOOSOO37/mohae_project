import React from "react";
import styled from "styled-components";
import Tag from "../parts/Tag";
import IconBtn from "../parts/IconButton";
import { IItemDetail } from "../../util/PostApi";
import { useNavigate } from "react-router";
import logo from "../../icons/logo.svg";
import { splitTag } from "../../function/validFn";

interface Props {
  item: IItemDetail;
}

function PostItem({ item }: Props) {
  const navigate = useNavigate();

  const thumbnailUrl = (item: IItemDetail) => {
    if (item.category === "맛집")
      return `${process.env.REACT_APP_S3_URL + item.imageKey}`;
    else return item.urls[0]?.thumbnail;
  };

  const onEnterPost = () => {
    if (sessionStorage.getItem("auth")) {
      navigate(`/postdetail/${item.id}`);
    } else {
      navigate("/login");
    }
  };
  return (
    <PostsSort onClick={onEnterPost}>
      <ImgWrapper>
        {thumbnailUrl(item) && <img src={thumbnailUrl(item)} alt="thumbnail" />}
      </ImgWrapper>
      <PostDetail>
        <TitleWrapper>
          <Title>{item.title}</Title>
        </TitleWrapper>
        <Summary>
          <div>{item.content}</div>
        </Summary>
        <TagWrapper>
          {splitTag(item.tags).map((x) => (
            <Tag key={x}>{x}</Tag>
          ))}
        </TagWrapper>
      </PostDetail>
      <IconSort>
        <IconBtn
          title={
            item.likeCount && item.likeCount > 0 ? `${item.likeCount}` : ""
          }
          width=""
          height="30px"
          radius="5px"
          fontWeight={400}
          fontColor="pink"
          btnType=""
          iconType={item.liked ? "fullheart" : "heart"}
          border="none"
          handleClick={() => {}}
        />
      </IconSort>
    </PostsSort>
  );
}

export default PostItem;

const PostDetail = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  align-items: flex-start;
  height: 100%;
  width: 70%;
  margin-left: 10px; /* 왼쪽 여백을 20px로 설정 */
  overflow: hidden;
`;

const TitleWrapper = styled.div`
  width: 100%;
  overflow: hidden;
  /* white-space: nowrap;
  text-overflow: ellipsis; */
`;

const ImgWrapper = styled.div`
  width: 15%;
  height: 80%;
  min-width: 80px;
  border-radius: 5px;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url("${logo}");
  background-position: center;
  background-repeat: no-repeat;

  > img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
`;

const Title = styled.div`
  /* width: 100%; */
  font-size: 1.5rem;
  font-weight: bold;
  color: white;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;
`;
const Summary = styled.div`
  font-size: 0.9rem;
  color: #5a5959;
  margin-bottom: 5px;
  height: 70px;
  margin-top: 5px;
  > div {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
  }
`;

const PostsSort = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between; /* space-between으로 변경 */
  align-items: center;
  width: 100%;
  height: 150px;
  border: 1px solid #4a4a4a;
  border-radius: 5px;
  background-color: #222222;
  padding: 16px; /* 3개의 간격을 주기 위해 padding을 추가 */
  gap: 16px;
`;

const IconSort = styled.div`
  display: flex;
  justify-content: center;
  align-items: flex-start;
  width: 60px;
  height: 100%;
`;

const TagWrapper = styled.div`
  display: flex;
  gap: 10px;
  overflow: hidden;
`;
