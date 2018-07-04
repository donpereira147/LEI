%option noyywrap
%x CON LTGT REGONCO RESULTCRIT INFORMED10 XXX10 XXX20 XXX30 NOTAS10 CONCLUS10 CONCLUS20 REGNEW
%{
	#include <ctype.h>
	#include <stdio.h>
	#include <string.h>
	char* regonco;
	char* resul;
	char* infor;
	char* x10;
	char* x20;
	char* conclu10;
	char* x30;
	char* conclu20;
	char* notas10;
%}

%%
"REGONCO" { ECHO;printf("->");BEGIN REGONCO;}
"RESULCRIT" {ECHO;printf("->");BEGIN RESULTCRIT;}
"INFORMED10" {ECHO;printf("->");BEGIN INFORMED10;}
"CONCLUS10" {ECHO;printf("->");BEGIN CONCLUS10;}
"CONCLUS20" {ECHO;printf("->");BEGIN CONCLUS20;}
"NOTAS10" {ECHO;printf("->");BEGIN NOTAS10;}
<REGONCO>[^r]+"r=" {BEGIN REGNEW;}
<REGNEW>[^/]+ {ECHO;printf("###");}
<REGNEW>.|\n {BEGIN INITIAL;}
<RESULTCRIT>[^r]+"lor=" {BEGIN REGNEW;}
<RESULTCRIT>.|\n {}
<INFORMED10>[^r]+"lor=" {BEGIN REGNEW;}
<INFORMED10>.|\n {}
<NOTAS10>[^>]+"r=" {BEGIN REGNEW;}
<NOTAS10>.|\n {}
<CONCLUS10>[^r]+"r=\"" {BEGIN CON;}
<CONCLUS20>[^r]+"r=\"" {BEGIN CON;}
<CON>"&lt;" {BEGIN LTGT;}
<LTGT>"&gt;" {BEGIN CON;}
<LTGT>.|\n {}
<CON>&[^;]+; {}
<CON>.|\n {ECHO;}
<CON>"/>" { printf("###");BEGIN INITIAL;}
<CON>"nbsp;" {}
"</RELATORIO>" {printf("-----\n");}
.|\n {}

%%

int main()
{
	yylex();
	return 0;
}