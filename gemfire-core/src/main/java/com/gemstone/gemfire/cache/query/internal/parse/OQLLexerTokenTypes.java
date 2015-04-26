// $ANTLR : "oql.g" -> "OQLParser.java"$

package com.gemstone.gemfire.cache.query.internal.parse;
import java.util.*;
import com.gemstone.gemfire.cache.query.internal.types.*;

public interface OQLLexerTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int TOK_RPAREN = 4;
	int TOK_LPAREN = 5;
	int TOK_COMMA = 6;
	int TOK_SEMIC = 7;
	int TOK_DOTDOT = 8;
	int TOK_COLON = 9;
	int TOK_DOT = 10;
	int TOK_INDIRECT = 11;
	int TOK_CONCAT = 12;
	int TOK_EQ = 13;
	int TOK_PLUS = 14;
	int TOK_MINUS = 15;
	int TOK_SLASH = 16;
	int TOK_STAR = 17;
	int TOK_LE = 18;
	int TOK_GE = 19;
	int TOK_NE = 20;
	int TOK_NE_ALT = 21;
	int TOK_LT = 22;
	int TOK_GT = 23;
	int TOK_LBRACK = 24;
	int TOK_RBRACK = 25;
	int TOK_DOLLAR = 26;
	int LETTER = 27;
	int DIGIT = 28;
	int ALL_UNICODE = 29;
	int NameFirstCharacter = 30;
	int NameCharacter = 31;
	int RegionNameCharacter = 32;
	int QuotedIdentifier = 33;
	int Identifier = 34;
	int RegionPath = 35;
	int NUM_INT = 36;
	int EXPONENT = 37;
	int FLOAT_SUFFIX = 38;
	int HEX_DIGIT = 39;
	int QUOTE = 40;
	int StringLiteral = 41;
	int WS = 42;
	int SL_COMMENT = 43;
	int ML_COMMENT = 44;
	int QUERY_PROGRAM = 45;
	int QUALIFIED_NAME = 46;
	int QUERY_PARAM = 47;
	int ITERATOR_DEF = 48;
	int PROJECTION_ATTRS = 49;
	int PROJECTION = 50;
	int TYPECAST = 51;
	int COMBO = 52;
	int METHOD_INV = 53;
	int POSTFIX = 54;
	int OBJ_CONSTRUCTOR = 55;
	int IMPORTS = 56;
	int SORT_CRITERION = 57;
	int LIMIT = 58;
	int HINT = 59;
	int LITERAL_trace = 60;
	int LITERAL_import = 61;
	int LITERAL_as = 62;
	int LITERAL_declare = 63;
	int LITERAL_define = 64;
	int LITERAL_query = 65;
	int LITERAL_undefine = 66;
	int LITERAL_select = 67;
	int LITERAL_distinct = 68;
	int LITERAL_all = 69;
	int LITERAL_from = 70;
	int LITERAL_in = 71;
	int LITERAL_type = 72;
	int LITERAL_where = 73;
	int LITERAL_limit = 74;
	int LITERAL_group = 75;
	int LITERAL_by = 76;
	int LITERAL_having = 77;
	int LITERAL_hint = 78;
	int LITERAL_order = 79;
	int LITERAL_asc = 80;
	int LITERAL_desc = 81;
	int LITERAL_or = 82;
	int LITERAL_orelse = 83;
	int LITERAL_and = 84;
	int LITERAL_for = 85;
	int LITERAL_exists = 86;
	int LITERAL_andthen = 87;
	int LITERAL_any = 88;
	int LITERAL_some = 89;
	int LITERAL_like = 90;
	int LITERAL_union = 91;
	int LITERAL_except = 92;
	int LITERAL_mod = 93;
	int LITERAL_intersect = 94;
	int LITERAL_abs = 95;
	int LITERAL_not = 96;
	int LITERAL_listtoset = 97;
	int LITERAL_element = 98;
	int LITERAL_flatten = 99;
	int LITERAL_nvl = 100;
	int LITERAL_to_date = 101;
	int LITERAL_first = 102;
	int LITERAL_last = 103;
	int LITERAL_unique = 104;
	int LITERAL_sum = 105;
	int LITERAL_min = 106;
	int LITERAL_max = 107;
	int LITERAL_avg = 108;
	int LITERAL_count = 109;
	int LITERAL_is_undefined = 110;
	int LITERAL_is_defined = 111;
	int LITERAL_struct = 112;
	int LITERAL_array = 113;
	int LITERAL_set = 114;
	int LITERAL_bag = 115;
	int LITERAL_list = 116;
	int LITERAL_short = 117;
	int LITERAL_long = 118;
	int LITERAL_int = 119;
	int LITERAL_float = 120;
	int LITERAL_double = 121;
	int LITERAL_char = 122;
	int LITERAL_string = 123;
	int LITERAL_boolean = 124;
	int LITERAL_byte = 125;
	int LITERAL_octet = 126;
	int LITERAL_enum = 127;
	int LITERAL_date = 128;
	int LITERAL_time = 129;
	int LITERAL_interval = 130;
	int LITERAL_timestamp = 131;
	int LITERAL_collection = 132;
	int LITERAL_dictionary = 133;
	int LITERAL_map = 134;
	int LITERAL_nil = 135;
	int LITERAL_null = 136;
	int LITERAL_undefined = 137;
	int LITERAL_true = 138;
	int LITERAL_false = 139;
	int NUM_LONG = 140;
	int NUM_FLOAT = 141;
	int NUM_DOUBLE = 142;
}
