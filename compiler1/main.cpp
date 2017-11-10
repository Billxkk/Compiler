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
//�ļ�ָ��
FILE *fp = NULL;
//����
int line = 1;
//����
int column = 1;
//�ؼ�������
string keyWord[10] = {"do","end","for","if","printf","scanf","then","while"};
const int KEYWORD_LENGTH = 10;
//����� ��ϵ�� ����
string opr[11] = {"+","-","++","*","/","<","<=","=",">",">=","<>"};
const int OPR_LASTPLACE = 4;
const int OPR_LENGTH = 11;
//�ֽ�� ����
string delim[6] = {",",";","(",")","[","]"};
const int DELIM_LENGTH = 6;

//��ʶ������
struct Name_list{
    struct Name_list* next;
    string name;
};
Name_list* nameHead = new Name_list();
//��������
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
    //���ļ�ָ��

    char inputChar;
    bool returnChar = false;//�Ƿ��з��ص��ַ�
    while(inputChar != EOF){
        if(!returnChar)
            inputChar = fgetc(fp);//��ȡ���ַ�
            returnChar = false;

        if(inputChar == ' ')//ʶ�𵽿ո�
            continue;
        else if( inputChar == '\n'){//ʶ��ת�з�
            line++;
            column = 1;//ÿһ�п�ʼʱ��ʼ����
            continue;
        }
        else if( 'A'<=inputChar&&inputChar<='Z' || 'a'<=inputChar&&inputChar<='z'){//ʶ����ĸ
            if( (inputChar = getLetter(inputChar)) != NULL)
                returnChar = true;
        }
        else if( '0'<=inputChar&&inputChar<='9'){//ʶ������
            if( (inputChar = getNumber(inputChar)) != NULL)
                returnChar = true;
        }
        else{    //ʶ��Ϊ �����ַ�
            if( (inputChar = getOthers(inputChar)) != NULL)
                returnChar = true;
        }
    }

    deleteTable();

    return 0;
}


/** \brief ʶ������ĸ������Ϊ�ؼ���or��ʶ��
 * \param inputChar ʶ����ַ�
 * \return ������ַ��򷵻أ����򷵻�ΪNULL
 */
char getLetter(char inputChar){
    string word = "";
    word.append(1,inputChar);

    /*��ȡ����������*/
    char getNext;//��ȡ��һ���ַ�
    while(getNext != EOF){
        getNext = fgetc(fp);
        if('A'<=getNext&&getNext<='Z' || 'a'<=getNext&&getNext<='z' ||
            '0'<=getNext&&getNext<='9'){//ʶ����ĸ������
            word.append(1,getNext);
        }
        else    //ʶ�𵽷����ֻ���ĸ
            break;
    }

    /*��ؼ��ֱ�*/
    for(int i = 0;i<KEYWORD_LENGTH;i++){
        if(keyWord[i] == word){
            print(word,TYPE_KEYWORD);
            column++;
            //eg:  for  (1,for)  �ؼ���  (1,1)

            //TODO :������ʵ� ����and���� ���洢�����ʶ�����
            return getNext;
        }
    }

    /*���ʶ����*/
    Name_list* pointer = nameHead->next;
    Name_list* createPointer = nameHead;
    while(pointer != NULL){
        if(pointer->name == word){
            print(word,TYPE_NAME);
            column++;

            //TODO :������ʵ� ����and���� ���洢�����ʶ�����
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

    //TODO :������ʵ� ����and���� ���洢�����ʶ�����
    return getNext;

}


/** \brief ʶ�������֣�����Ϊ����
 * \param inputChar ʶ����ַ�
 * \return ������ַ��򷵻أ����򷵻�ΪNULL
 */
char getNumber(char inputChar){
    string word = "";
    word.append(1,inputChar);

    /*��ȡ����������*/
    char getNext;//��ȡ��һ���ַ�
    while(getNext != EOF){
        getNext = fgetc(fp);
        if('0'<=getNext&&getNext<='9'){//ʶ������
            word.append(1,getNext);
        }else if('A'<=getNext&&getNext<='Z' || 'a'<=getNext&&getNext<='z'){//ʶ����ĸ
            word.append(1,getNext);
            print(word,ERROR);
            column++;
            return NULL;
        }else   //ʶ�� �ֽ�� ����� ��ϵ�� �ȵ�
            break;
    }

    /*�鳣����*/
    Number_list* pointer = numberHead->next;
    Number_list* createPointer = numberHead;
    while(pointer != NULL){
        if(pointer->number == word){
            print(word,TYPE_NUM);
            column++;

            //TODO :������ʵ� ����and���� ���洢�����ʶ�����
            return getNext;
        }else
            createPointer = pointer;
            pointer = pointer->next;
    }
    createPointer->next = new Number_list();
    createPointer->next->number = word;
    print(word,TYPE_NUM);
    column++;

    //TODO :������ʵ� ����and���� ���洢�����ʶ�����
    return getNext;

}

/** \brief ʶ���������ַ�������Ϊע�� or ��ϵ���ֽ������� or ����
 *          ע�� : Ŀǰ��ʵ��ʶ��1��2���ַ���ɵĹ�ϵ�� �����  ; �ֽ����ʶ��1���ַ�
 * \param inputChar ʶ����ַ�
 * \return ������ַ��򷵻أ����򷵻�ΪNULL
 */
char getOthers(char inputChar){
    string word = "";
    word.append(1,inputChar);

    /*�� �ֽ���� */
    for(int i = 0;i<DELIM_LENGTH;i++){
        if(delim[i] == word){
            print(word,TYPE_DELIM);
            column++;

            //TODO :������ʵ� ����and���� ���洢�����ʶ�����
            return NULL;
        }
    }

    /*�� ����� ��ϵ�� �� */
    char getNext = fgetc(fp);
    word.append(1,getNext);
    for(int i = 0;i<OPR_LENGTH;i++){
        if(opr[i] == word){
            if(i<OPR_LASTPLACE+1)
                print(word,TYPE_OPR);
            else
                print(word,TYPE_RELATION);
            column++;

            //TODO :������ʵ� ����and���� ���洢�����ʶ�����
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

            //TODO :������ʵ� ����and���� ���洢�����ʶ�����
            return getNext;
        }
    }

    /*�Ƿ��ַ� */
    if(inputChar != EOF)
        print(word,ERROR);
    column++;
    return getNext;

}

/** \brief ���ձ�ʶ���� �� ������ �������ڴ�
 */
 void deleteTable(){

 }

 /** \brief ���
 */
void print(string word,int type){
    switch(type){
        case TYPE_KEYWORD:
            cout<<word<<  "\t(1,"<<word<<")"  << "�ؼ���"  <<  "\t\t("<<line<<","<<column<<")"  <<"\n";
            break;
        case TYPE_NAME:
            cout<<word<<  "\t(6,"<<word<<")"  << "��ʶ��"  <<  "\t\t("<<line<<","<<column<<")"  <<"\n";
            break;
        case TYPE_NUM:
            cout<<word<<  "\t(5,"<<word<<")"  << "����"  <<  "\t\t("<<line<<","<<column<<")"  <<"\n";
            break;
        case ERROR:
            cout<<word<<  "\tERROR"  <<  "\t\t\t("<<line<<","<<column<<")"  <<"\n";
            break;
        case TYPE_DELIM:
            cout<<word<<  "\t(2,"<<word<<")"  << "�ֽ��"  <<  "\t\t("<<line<<","<<column<<")"  <<"\n";
            break;
        case TYPE_OPR:
            cout<<word<<  "\t(3,"<<word<<")"  << "�����"  <<  "\t\t("<<line<<","<<column<<")"  <<"\n";
            break;
        case TYPE_RELATION:
            cout<<word<<  "\t(4,"<<word<<")"  << "��ϵ��"  <<  "\t\t("<<line<<","<<column<<")"  <<"\n";
            break;
    }
}




