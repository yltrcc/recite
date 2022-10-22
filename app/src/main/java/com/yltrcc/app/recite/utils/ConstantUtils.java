package com.yltrcc.app.recite.utils;


public class ConstantUtils {

    public static final String BASE_API = "http://129.28.156.166:10000/api";
    //public static final String BASE_API = "http://127.0.0.1:8081/api";

    //面试题
    public static final String QUESTION_QUERYALLV3 = "/question/queryAllCategoryV3";
    public static final String QUESTION_QUESTION_BY_SUB = "/question/queryQuestionBySubCategoryId";
    public static final String QUESTION_QUESTION_BY_CATEGORY_ID = "/question/queryQuestionByCategoryId";
    public static final String UPDATE_CONTENT = "/question/updateContent";

    //算法
    public static final String QUESTION_QUESTION_ALL_ALGORITHM = "/question/queryAllAlgorithm";


    //版本更新相关API
    public static final String UPDATE_VERIFY_URL = "https://gitee.com/yltrcc/recite/raw/master/apk/1.txt";
    public static final String UPDATE_URL = "https://gitee.com/login?authenticity_token=o45xyqIcAFBawNimz1vAbNEkQM/lQ2/QThYC0VgPszF2Zg6FoROPG5eJCBra7HsgqS5W1yq9H+dG4GB+S7jXMA==&redirect_to_url=/yltrcc/recite/raw/master/apk/app-debug.apk&user[login]=ttxxly@gmail.com&encrypt_data[user[password]]=gAMMz5uSfgUqJ/NjU8b12Mv9qYMJ1p0wUROmdNEJKRDq7n3dg3nw5uNMteHUiub56rNPf80HMyHkX3nCkK4i5lUlOMOV8IKRvY/MZWRzpeqh7eqHYqettm07b3NH2cZJJUE7v5BvXtQro/XTdjejLPjl2/o82ZVmTww9letVcGI=";

    //文章相关API
    public static final String ARTICLE_QUERY_ALL_V3 = "/article/queryAllCategoryV3";
    public static final String QUERY_ARTICLE = "/article/queryArticleByCategoryId";

    //随机生成公众号文章
    public static final String RANDOM_ARTICLE = "https://www.ylcoder.top/api/random/getArticle";

}
