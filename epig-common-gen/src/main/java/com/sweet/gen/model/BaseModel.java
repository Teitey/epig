package com.sweet.gen.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import com.sweet.gen.pagination.Pagination;
import org.apache.commons.beanutils.BeanUtils;



/**
 * 所有的model对象的父类，提供基本的分页功能等其他功能
 */
public abstract class BaseModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 分页参数.
	 */
	private Pagination pagination;

	public Pagination getPagination() {
		return pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

	/**
     * @return
     *
     * @see Object#toString()
     */
    public String toString() {
        try {
            Map          props     = BeanUtils.describe(this);
            Iterator     iProps    = props.keySet().iterator();
            StringBuffer retBuffer = new StringBuffer();

            while (iProps.hasNext()) {
                String key = (String) iProps.next();

                // skip false property "class"
                if ("class".equals(key)) {
                    continue;
                }

                retBuffer.append(key).append("=[").append(props.get(key)).append("]");

                if (iProps.hasNext()) {
                    retBuffer.append(", ");
                }
            }

            return retBuffer.toString();
        } catch (Exception e) {
            return super.toString();
        }
    }

}
