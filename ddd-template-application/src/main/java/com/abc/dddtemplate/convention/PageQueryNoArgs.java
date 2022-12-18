package com.abc.dddtemplate.convention;

import com.abc.dddtemplate.share.dto.PageData;

/**
 * @author <template/>
 * @date
 */
public abstract class PageQueryNoArgs<RESULT> implements PageQuery<Void, RESULT> {
    @Override
    public PageData<RESULT> exec(Void aVoid) {
        return query();
    }

    public abstract PageData<RESULT> query();
}