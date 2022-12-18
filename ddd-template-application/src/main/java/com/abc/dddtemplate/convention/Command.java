package com.abc.dddtemplate.convention;

/**
 * @author <template/>
 * @date
 */
public interface Command<PARAM, RESULT> {
    RESULT exec(PARAM param);
}
