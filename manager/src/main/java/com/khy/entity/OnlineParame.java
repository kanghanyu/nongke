package com.khy.entity;

import java.io.Serializable;
import java.util.Date;

public class OnlineParame implements Serializable {
    private Long id;

    private String title;

    private String content;
    
    private String description;

    private Date createTime;
    
    
    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }
    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}