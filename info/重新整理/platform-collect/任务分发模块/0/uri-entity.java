@Document(collection = "uri_collect")
@Data
@EqualsAndHashCode(callSuper = true)
@CompoundIndexes({
    @CompoundIndex(name = "uri_unique_idx", 
                  def = "{'uri': 1, 'rootNode': 1, 'versionType': 1, 'uriVersion': 1}", 
                  unique = true),
    @CompoundIndex(name = "uriHash_idx", 
                  def = "{'uriHash': 1}", 
                  unique = true)
})
public class UriEntity extends VersionEntity {
    @Indexed(unique = true)
    private String uriHash;
    
    @Indexed
    private String uri;
    private String rootNode;
    private String versionType;
    private String uriVersion;
    private Map<String, Object> details;
    
    @Override
    public void prePersist() {
        if (this.uriHash == null && this.uri != null) {
            this.uriHash = HashUtil.hash(this.uri);
        }
        this.version = 0L;
    }
}