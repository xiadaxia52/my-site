package cn.luischen.oss;

/**
 * MinioFile
 *
 * @author DaiBo
 * @since 2023-05-19
 */
public class MinioFile {
    private String link;
    private String domain;
    private String name;
    private String originalName;
    private Long attachId;

    public MinioFile() {
    }

    public String getLink() {
        return this.link;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getName() {
        return this.name;
    }

    public String getOriginalName() {
        return this.originalName;
    }

    public Long getAttachId() {
        return this.attachId;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setOriginalName(final String originalName) {
        this.originalName = originalName;
    }

    public void setAttachId(final Long attachId) {
        this.attachId = attachId;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof MinioFile)) {
            return false;
        } else {
            MinioFile other = (MinioFile) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label71:
                {
                    Object this$attachId = this.getAttachId();
                    Object other$attachId = other.getAttachId();
                    if (this$attachId == null) {
                        if (other$attachId == null) {
                            break label71;
                        }
                    } else if (this$attachId.equals(other$attachId)) {
                        break label71;
                    }

                    return false;
                }

                Object this$link = this.getLink();
                Object other$link = other.getLink();
                if (this$link == null) {
                    if (other$link != null) {
                        return false;
                    }
                } else if (!this$link.equals(other$link)) {
                    return false;
                }

                label57:
                {
                    Object this$domain = this.getDomain();
                    Object other$domain = other.getDomain();
                    if (this$domain == null) {
                        if (other$domain == null) {
                            break label57;
                        }
                    } else if (this$domain.equals(other$domain)) {
                        break label57;
                    }

                    return false;
                }

                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name != null) {
                        return false;
                    }
                } else if (!this$name.equals(other$name)) {
                    return false;
                }

                Object this$originalName = this.getOriginalName();
                Object other$originalName = other.getOriginalName();
                if (this$originalName == null) {
                    if (other$originalName == null) {
                        return true;
                    }
                } else if (this$originalName.equals(other$originalName)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof MinioFile;
    }

    @Override
    public int hashCode() {
        int result = 1;
        Object $attachId = this.getAttachId();
        result = result * 59 + ($attachId == null ? 43 : $attachId.hashCode());
        Object $link = this.getLink();
        result = result * 59 + ($link == null ? 43 : $link.hashCode());
        Object $domain = this.getDomain();
        result = result * 59 + ($domain == null ? 43 : $domain.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $originalName = this.getOriginalName();
        result = result * 59 + ($originalName == null ? 43 : $originalName.hashCode());
        return result;
    }

    public String toString() {
        return "MinioFile(link=" + this.getLink() + ", domain=" + this.getDomain() + ", name=" + this.getName() + ", originalName=" + this.getOriginalName() + ", attachId=" + this.getAttachId() + ")";
    }
}
