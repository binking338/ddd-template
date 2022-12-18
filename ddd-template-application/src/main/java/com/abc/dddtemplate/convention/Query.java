package com.abc.dddtemplate.convention;

/**
 * @author <template/>
 * @date
 */
public interface Query<PARAM, RESULT> {
    RESULT exec(PARAM param);
}
