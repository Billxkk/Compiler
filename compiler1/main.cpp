#include <stdio.h>
#include <string>
#include <iostream>
#include <string.h>

using namespace std;

const int TYPE_KEYWORD = 1;
const int TYPE_DELIM = 2;
const int TYPE_OPR = 3;
const int TYPE_RELATION = 4;
const int TYPE_NUM = 5;
const int TYPE_NAME = 6;
const int ERROR = -1;
//文件指针
FILE *fp = NULL;
//行数
int line = 1;
//列数
int column = 1;
//关键字数组
string keyWord[10] = {"do","end","for","if","printf","scanf","then","while"};
const int KEYWORD_LENGTH = 10;
//运算符 关系符 数组
string opr[11] = {"+","-","++","*","/","<","<=","=",">",">=","<>"};
const int OPR_LASTPLACE = 4;
const int OPR_LENGTH = 11;
//分界符 数组
string delim[6] = {",",";","(",")","[","]"};
const int DELIM_LENGTH = 6;

//标识符链表
struct Name_list{
    struct Name_list* next;
    string name;
};
Name_list* nameHead = new Name_list();
//常数链表
struct Number_list{
    struct Number_list* next;
    string number;
};
Number_list* numberHead = new Number_list();


char getLetter(char inputChar);
char getNumber(char inputChar);
char getOthers(char inputChar);
void deleteTable();
void print(string word,int type);

int main()
{
    fp = fopen("data.txt", "r");
    //打开文件指针

    char inputChar;
    bool returnChar = false;//是否有返回的字符
    while(inputChar != EOF){
        if(!returnChar)
            inputChar = fgetc(fp);//获取到字符
            returnChar = false;

        if(inputChar == ' ')//识别到空格
            continue;
        else if( inputChar == '\n'){//识别到转行符
            line++;
            column = 1;//每一行开始时初始化列
            continue;
        }
        else if( 'A'<=inputChar&&inputChar<='Z' || 'a'<=inputChar&&inputChar<='z'){//识别到字母
            if( (inputChar = getLetter(inputChar)) != NULL)
                returnChar = true;
        }
        else if( '0'<=inputChar&&inputChar<='9'){//识别到数字
            if( (inputChar = getNumber(inputChar)) != NULL)
                returnChar = true;
        }
        else{    //识别为 其他字符
            if( (inputChar = getOthers(inputChar)) != NULL)
                returnChar = true;
        }
    }

    deleteTable();

    return 0;
}


/** \brief 识别到了字母，处理为关键字or标识符
 * \param inputChar 识别的字符
 * \return 多吃了字符则返回，否则返回为NULL
 */
char getLetter(char inputChar){
    string word = "";
    word.append(1,inputChar);

    /*获取到整个单词*/
    char getNext;//获取下一个字符
    while(getNext != EOF){
        getNext = fgetc(fp);
        if('A'<=getNext&&getNext<='Z' || 'a'<=getNext&&getNext<='z' ||
            '0'<=getNext&&getNext<='9'){//识别到字母或数字
            word.append(1,getNext);
        }
        else    //识别到非数字或字母
            break;
    }

    /*查关键字表*/
    for(int i = 0;i<KEYWORD_LENGTH;i++){
        if(keyWord[i] == word){
            print(word,TYPE_KEYWORD);
            column++;
            //eg:  for  (1,for)  关键字  (1,1)

            //TODO :输出单词的 内码and属性 ，存储到单词对象中
            return getNext;
        }
    }

    /*查标识符表*/
    Name_list* pointer = nameHead->next;
    Name_list* createPointer = nameHead;
    while(pointer != NULL){
        if(pointer->name == word){
            print(word,TYPE_NAME);
            column++;

            //TODO :输出单词的 内码and属性 ，存储到单词对象中
            return getNext;
        }else{
            createPointer = pointer;
            pointer = pointer->next;
        }
    }
    createPointer->next = new Name_list();
    createPointer->next->name = word;
    print(word,TYPE_NAME);
    column++;

    //TODO :输出单词的 内码and属性 ，存储到单词对象中
    return getNext;

}


/** \brief 识别到了数字，处理为常数
 * \param inputChar 识别的字符
 * \return 多吃了字符则返回，否则返回为NULL
 */
char getNumber(char inputChar){
    string word = "";
    word.append(1,inputChar);

    /*获取到整个单词*/
    char getNext;//获取下一个字符
    while(getNext != EOF){
        getNext = fgetc(fp);
        if('0'<=getNext&&getNext<='9'){//识别到数字
            word.append(1,getNext);
        }else if('A'<=getNext&&getNext<='Z' || 'a'<=getNext&&getNext<='z'){//识别到字母
            word.append(1,getNext);
            print(word,ERROR);
            column++;
            return NULL;
        }else   //识别到 分界符 运算符 关系符 等等
            break;
    }

    /*查常数表*/
    Number_list* pointer = numberHead->next;
    Number_list* createPointer = numberHead;
    while(pointer != NULL){
        if(pointer->number == word){
            print(word,TYPE_NUM);
            column++;

            //TODO :输出单词的 内码and属性 ，存储到单词对象中
            return getNext;
        }else
            createPointer = pointer;
            pointer = pointer->next;
    }
    createPointer->next = new Number_list();
    createPointer->next->number = word;
    print(word,TYPE_NUM);
    column++;

    //TODO :输出单词的 内码and属性 ，存储到单词对象中
    return getNext;

}

/** \brief 识别到了其他字符，处理为注解 or 关系符分界符运算符 or 错误
 *          注意 : 目前仅实现识别1到2个字符组成的关系符 运算符  ; 分界符仅识别1个字符
 * \param inputChar 识别的字符
 * \return 多吃了字符则返回，否则返回为NULL
 */
char getOthers(char inputChar){
    string word = "";
    word.append(1,inputChar);

    /*查 分界符表 */
    for(int i = 0;i<DELIM_LENGTH;i++){
        if(delim[i] == word){
            print(word,TYPE_DELIM);
            column++;

            //TODO :输出单词的 内码and属性 ，存储到单词对象中
            return NULL;
        }
    }

    /*查 运算符 关系符 表 */
    char getNext = fgetc(fp);
    word.append(1,getNext);
    for(int i = 0;i<OPR_LENGTH;i++){
        if(opr[i] == word){
            if(i<OPR_LASTPLACE+1)
                print(word,TYPE_OPR);
            else
                print(word,TYPE_RELATION);
            column++;

            //TODO :输出单词的 内码and属性 ，存储到单词对象中
            return NULL;
        }
    }
    word.erase(1);
    for(int i = 0;i<OPR_LENGTH;i++){
        if(opr[i] == word){
            if(i<OPR_LASTPLACE+1)
                print(word,TYPE_OPR);
            else
                print(word,TYPE_RELATION);
            column++;

            //TODO :输出单词的 内码and属性 ，存储到单词对象中
            return getNext;
        }
    }

    /*非法字符 */
    if(inputChar != EOF)
        print(word,ERROR);
    column++;
    return getNext;

}

/** \brief 回收标识符表 和 常数表 的申请内存
 */
 void deleteTable(){

 }

 /** \brief 输出
 */
void print(string word,int type){
    switch(type){
        case TYPE_KEYWORD:
            cout<<word<<  "\t(1,"<<word<<")"  << "关键字"  <<  "\t\t("<<line<<","<<column<<")"  <<"\n";
            break;
        case TYPE_NAME:
            cout<<word<<  "\t(6,"<<word<<")"  << "标识符"  <<  "\t\t("<<line<<","<<column<<")"  <<"\n";
            break;
        case TYPE_NUM:
            cout<<word<<  "\t(5,"<<word<<")"  << "常数"  <<  "\t\t("<<line<<","<<column<<")"  <<"\n";
            break;
        case ERROR:
            cout<<word<<  "\tERROR"  <<  "\t\t\t("<<line<<","<<column<<")"  <<"\n";
            break;
        case TYPE_DELIM:
            cout<<word<<  "\t(2,"<<word<<")"  << "分界符"  <<  "\t\t("<<line<<","<<column<<")"  <<"\n";
            break;
        case TYPE_OPR:
            cout<<word<<  "\t(3,"<<word<<")"  << "运算符"  <<  "\t\t("<<line<<","<<column<<")"  <<"\n";
            break;
        case TYPE_RELATION:
            cout<<word<<  "\t(4,"<<word<<")"  << "关系符"  <<  "\t\t("<<line<<","<<column<<")"  <<"\n";
            break;
    }
}




