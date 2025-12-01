package ac.kr.mjc.capstone.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE("C001", "잘못된 입력 값입니다"),
    INVALID_VERIFICATION_CODE("C400", "유효하지 않은 인증 코드입니다"),
    EXPIRED_VERIFICATION_CODE("C401", "인증 코드가 만료되었습니다"),
    INTERNAL_SERVER_ERROR("C999", "서버 내부 오류가 발생했습니다"),

    // Auth
    INVALID_TOKEN("AUTH001", "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN("AUTH002", "만료된 토큰입니다"),
    UNAUTHORIZED("AUTH003", "인증이 필요합니다"),
    INVALID_CREDENTIALS("AUTH004", "아이디 또는 비밀번호가 일치하지 않습니다"),
    TOKEN_NOT_FOUND("AUTH005", "토큰을 찾을 수 없습니다"),
    LOGIN_FAILED("AUTH006", "로그인 실패"),

    // User
    USER_NOT_FOUND("USER001", "사용자를 찾을 수 없습니다"),
    DUPLICATE_EMAIL("USER002", "이미 사용 중인 이메일입니다"),
    DUPLICATE_USERNAME("USER003", "이미 사용 중인 사용자 이름입니다"),
    USER_VERIFICATION_FAILED("USER004", "사용자 인증에 실패했습니다"),
    PASSWORD_MISMATCH("USER005", "비밀번호가 일치하지 않습니다"),
    SOCIAL_USER_CANNOT_CHANGE_PASSWORD("USER006", "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다"),

    // Board
    BOARD_NOT_FOUND("BOARD001", "게시글을 찾을 수 없습니다"),
    FORBIDDEN("BOARD002", "권한이 없습니다"),

    // Reply
    REPLY_NOT_FOUND("REPLY001", "댓글을 찾을 수 없습니다"),

    // Notice
    NOTICE_NOT_FOUND("NOTICE001", "공지사항을 찾을 수 없습니다"),

    // BoardImage
    IMAGE_NOT_FOUND("IMAGE001", "이미지를 찾을 수 없습니다"),
    IMAGE_UPLOAD_FAILED("IMAGE002", "이미지 업로드에 실패했습니다"),
    INVALID_IMAGE_FORMAT("IMAGE003", "지원하지 않는 이미지 형식입니다"),

    // Child
    CHILD_NOT_FOUND("CHILD001", "자녀를 찾을 수 없습니다"),
    CHILD_ID_MISMATCH("CHILD002", "자녀와 ID가 일치하지 않습니다."),

    // Book
    BOOK_NOT_FOUND("BOOK001","도서를 찾을 수 없습니다."),

    // BookDetails
    BOOKDETAILS_NOT_FOUND("BOOKDETAILS001","도서 상세를 찾을 수 없습니다."),

    // Reader
    READER_NOT_FOUND("READER001","독자를 찾을 수 없습니다."),
    UNAUTHORIZED_READER_OWNERSHIP("READER002", "reader에 해당 user가 속해있지 않습니다."),
    READER_CHILD_MISMATCH("READER003","reader와 child가 일치하지 않습니다."),
    READER_ALREADY_EXISTS("READER003","해당 자녀에 연결된 독자 정보가 이미 존재합니다."),

    //contest
    CONTEST_NOT_FOUND("CONTEST001","대회를 찾을 수 없습니다."),

    //contestDetails
    CONTEST_DETAILS_NOT_FOUND("CONTEST_DETAILS001","대회 상세를 찾을 수 없습니다."),

    //story
    STORY_NOT_FOUND("STORY001", "이어쓰기를 찾을 수 없습니다."),

    //Vote
    ALREADY_VOTED("VOTE001","이미 투표하였습니다."),
    ALREADY_EXISTS("VOTE002","자신에게 투표할 수 없습니다.");

    private final String code;
    private final String message;
}
