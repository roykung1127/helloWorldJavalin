package app.utils;

public class VodDetail {

	// Constant
	public static final String PAYMENT_TYPE_FREE = "FREE";
	
	//require field
	public String webImgENPath = null;
	public String webImgZHPath = null;
	public String classification = null;
	public boolean isAppRestricted = true;
	public long duration  = 0;
	public String cpid = null;
	public String productPaymentType = null;
	public String productId = null;
	
	public String productDisplayOnOnAir = null;

	
	//optional field
	public String productSubsidiary1 = null;
	public String productSubsidiary2 = null;
	public String productSubsidiary3 = null;
	public String productSubsidiary4 = null;
	public String productSubsidiary5 = null;
	public String productType = null;
	public boolean isWEB = false;
	public boolean isHLS = false;
	public boolean isFLV = false;
	public long scheduleStartTime = 0 ;
	public long scheduleEndTime = 0;
	public String scheduleStatus = null;
	public String seriesId = null;
	
	//mongo data
	public String libId = null;
	public String libNameEn = null;
	public String libNameZh = null;
	public String paymentType = null;
	
	
	//NPVR
	public String npvrId = null;
	public String webAssetId = null;
	public String hlsAssetId = null;
	
	public long jumpstart = 0;
	
	public String preroll = null;
	
	public long getJumpstart() {
		return jumpstart;
	}

	public void setJumpstart(long jumpstart) {
		this.jumpstart = jumpstart;
	}

	//get Field
	//require field
	public String getWebImgENPath(){
		return webImgENPath;
	}
	
	public String getWebImgZHPath(){
		return webImgZHPath;
	}
	public String getClassification(){
		return classification;
	}
	public boolean getIsAppRestricted(){
		return isAppRestricted;
	}
	public long getDuration(){
		return duration;
	}
	public String getCpid(){
		return cpid;
	}
	public String getProductPaymentType(){
		return productPaymentType;
	}
	public String getProductId(){
		return productId;
	}
	//optional field
	public String getProductSubsidiary1(){
		return productSubsidiary1;
	}
	public String getProductSubsidiary2(){
		return productSubsidiary2;
	}
	public String getProductSubsidiary3(){
		return productSubsidiary3;
	}
	public String getProductSubsidiary4(){
		return productSubsidiary4;
	}
	public String getProductSubsidiary5(){
		return productSubsidiary5;
	}
	public String getProductType(){
		return productType;
	}
	public boolean getIsWEB(){
		return isWEB;
	}
	public boolean getIsHLS(){
		return isHLS;
	}
	public boolean getIsFLV(){
		return isFLV;
	}
	public String getProductDisplayOnOnAir() {
		return productDisplayOnOnAir;
	}
	public long getScheduleStartTime(){
		return scheduleStartTime;
	}
	public long getScheduleEndTime(){
		return scheduleEndTime;
	}
	public String getScheduleStatus(){
		return scheduleStatus;
	}

	public String getLibId() {
		return libId;
	}
	
	public String getLibNameEn() {
		return libNameEn;
	}

	public String getLibNameZh() {
		return libNameZh;
	}

	public String getPaymentType() {
		return paymentType;
	}

	//NPVR
	public String getNpvrId() {
		if(webAssetId == null || "".equals(webAssetId)){
			return null;
		}
		if(hlsAssetId == null || "".equals(hlsAssetId)){
			return null;
		}
		if("".equals(npvrId)){
			return null;
		}
		return npvrId;
	}
	
	public String getWebAssetId() {
		return webAssetId;
	}
	
	public String getHlsAssetId() {
		return hlsAssetId;
	}
	
	//set Field
	//required field 
	public void setWebImgENPath(String in){
		webImgENPath = in;
	}
	public void setWebImgZHPath(String in){
		webImgZHPath = in;
	}
	public void setClassification(String in){
		classification = in;
	}
	public void setIsAppRestricted(boolean in){
		isAppRestricted = in;
	}
	public void setDuration(long in){
		duration = in;
	}
	public void setCpid(String in){
		cpid = in;
	}
	public void setProductPaymentType(String in){
		productPaymentType = in;
	}
	public void setProductId(String in){
		productId = in;
	}
	//optional field
	public void setProductSubsidiary1(String in){
		productSubsidiary1 = in;
	}
	public void setProductSubsidiary2(String in){
		productSubsidiary2 = in;
	}
	public void setProductSubsidiary3(String in){
		productSubsidiary3 = in;
	}
	public void setProductSubsidiary4(String in){
		productSubsidiary4 = in;
	}
	public void setProductSubsidiary5(String in){
		productSubsidiary5 = in;
	}
	public void setProductType(String in){
		productType = in;
	}
	public void setIsWEB(boolean in){
		isWEB = in;
	}
	public void setIsHLS(boolean in){
		isHLS = in;
	}
	public void setIsFLV(boolean in){
		isFLV = in;
	}
	public void setScheduleStartTime(long in){
		scheduleStartTime = in;
	}
	public void setScheduleEndTime(long in){
		scheduleEndTime = in;
	}
	public void setScheduleStatus(String in){
		scheduleStatus = in;
	}

	public void setProductDisplayOnOnAir(String productDisplayOnOnAir) {
		this.productDisplayOnOnAir = productDisplayOnOnAir;
	}

	public void setLibId(String libId) {
		this.libId = libId;
	}

	public void setLibNameEn(String libNameEn) {
		this.libNameEn = libNameEn;
	}
	public void setLibNameZh(String libNameZh) {
		this.libNameZh = libNameZh;
	}
	
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	
	//NPVR
	public void setNpvrId(String npvrId) {
		this.npvrId = npvrId;
	}
	public void setHlsAssetId(String hlsAssetId) {
		this.hlsAssetId = hlsAssetId;
	}
	public void setWebAssetId(String webAssetId) {
		this.webAssetId = webAssetId;
	}

	public String getSeriesId() {
		return seriesId;
	}

	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}

	public String getPreroll() {
		return preroll;
	}

	public void setPreroll(String preroll) {
		this.preroll = preroll;
	}

	@Override
	public String toString() {
		return "VodDetail [webImgENPath=" + webImgENPath + ", webImgZHPath=" + webImgZHPath + ", classification="
				+ classification + ", isAppRestricted=" + isAppRestricted + ", duration=" + duration + ", cpid=" + cpid
				+ ", productPaymentType=" + productPaymentType + ", productId=" + productId + ", productDisplayOnOnAir="
				+ productDisplayOnOnAir + ", productSubsidiary1=" + productSubsidiary1 + ", productSubsidiary2="
				+ productSubsidiary2 + ", productSubsidiary3=" + productSubsidiary3 + ", productSubsidiary4="
				+ productSubsidiary4 + ", productSubsidiary5=" + productSubsidiary5 + ", productType=" + productType
				+ ", isWEB=" + isWEB + ", isHLS=" + isHLS + ", isFLV=" + isFLV + ", scheduleStartTime="
				+ scheduleStartTime + ", scheduleEndTime=" + scheduleEndTime + ", scheduleStatus=" + scheduleStatus
				+ ", seriesId=" + seriesId + ", libId=" + libId + ", libNameEn=" + libNameEn + ", libNameZh="
				+ libNameZh + ", paymentType=" + paymentType + ", npvrId=" + npvrId + ", webAssetId=" + webAssetId
				+ ", hlsAssetId=" + hlsAssetId + ", jumpstart=" + jumpstart + ", preroll=" + preroll + "]";
	}	
}
