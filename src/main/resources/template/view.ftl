<style scoped lang="less">
@import '../../styles/common.less';
</style>
<template>
    <div class="layout-content">
        <div class="layout-content-main">
            <div class="layout-tools">
                <#nt>
                <#list args as arg>
                <#if arg.type == "Date">
                <div style="display:inline;">
                    <span>${arg.remark}：</span>
                    <DatePicker size="small" type="datetime" format="yyyy-MM-dd HH:mm" v-model="${arg.name}_start" placeholder="选择开始日期" style="width:136px;"></DatePicker>
                    <DatePicker size="small" type="datetime" format="yyyy-MM-dd HH:mm" v-model="${arg.name}_end" placeholder="选择结束日期" style="width:136px;"></DatePicker>
                </div>
                <#else>
                <div style="display:inline;">
                    <span>${arg.remark}：</span>
                    <Input size="small" v-model="${arg.name}" style="width:120px;"></Input>
                </div>
                </#if>
                </#list>
                <#if (args?size > 0)>
                <div style="display:inline;">
                    <Button type="primary" size="small" icon="ios-search" @click="query">查询</Button>
                </div>
                </#if>
                <Row style="margin-top: 10px;">
                    <Col span="24">
                    <permissionButton type="primary" size="small" icon="plus" v-on:increment="addItem()" perStr="admin:${name}:add" text="新增"></permissionButton>
                    </Col>
                </Row>
            </div>
            <Table stripe :columns="columns" :data="data"></Table>
            <div style="margin: 10px;overflow: hidden">
                <div style="float: right;">
                    <Page :total="total" size="small" :page-size="pageSize" show-elevator show-sizer @on-change="pageChange" @on-page-size-change="pageSizeChange"></Page>
                </div>
            </div>
            <Modal v-model="showModel" :title="modelTitle" :mask-closable="false" :width="540" :closable="false">
                <Form ref="form-${name}" :model="formInline" :rules="ruleInline" inline style="padding-right:30px;">
                    <#list attrs as attr>
                    <#if attr.edit>
                    <div style="width:50%;float: left;">
                        <FormItem prop="${attr.name}" label="${attr.remark}：" :label-width="80">
                            <#if attr.type == "int" || attr.type == "double" || attr.type == "long" || attr.type == "float" >
                            <InputNumber :min="0" v-model="formInline.${attr.name}" placeholder="请输入"></InputNumber>
                            <#else>
                            <Input v-model="formInline.${attr.name}" placeholder="请输入"></Input>
                            </#if>
                        </FormItem>
                    </div>
                    </#if>
                    </#list>
                    <div style="clear: both;"></div>
                    <Row>
                        <Col span="12" style="text-align:right;">
                        <FormItem style="margin-bottom: 0px;">
                            <Button size="large" @click="modelCancel">取消</Button>
                        </FormItem>
                        </Col>
                        <Col span="12" style="text-align:left;padding-left:30px;">
                        <FormItem style="margin-bottom: 0px;">
                            <Button type="primary" size="large" :loading="isSaveing" @click="handleSubmit('form-${name}')">保存</Button>
                        </FormItem>
                        </Col>
                    </Row>
                </Form>
                <div slot="footer"></div>
            </Modal>
        </div>
    </div>
</template>

<script>
    import util from '../../libs/util';
    import permissionButton from '../main_components/button'

    export default{
        components:{
            permissionButton
        },
        data(){
            return {
                addOrUpdate:'add',
                showModel:false,
                isSaveing:false,
                modelTitle:'新增${remark}',
                formInline:{
                    <#list attrs as attr>
                    <#if attr.edit>
                    ${attr.name}:<#if attr.type == "int" || attr.type == "double" || attr.type == "long" || attr.type == "float" >0<#else>''</#if>,
                    </#if>
                    </#list>
                    ${primaryKey}:0
                },
                ruleInline:{

                },
                columns:[
                    { title:'编号',key:'index',type:'index',align:'center'},
                    <#list attrs as attr>
                    <#if attr.show>
                    { title:'${attr.remark}',key:'${attr.name}',align:'center'<#if attr.type=="Date">,render:(h,params)=>{
                        return h('Span',{},util.formatDate(new Date(params.row.${attr.name}),'yyyy-MM-dd hh:mm:ss'));
                    }</#if>},
                    </#if>
                    </#list>
                    { title:'操作',key:'${primaryKey}',align:'center',width:180,render:(h,params)=>{
                        return h('div',[
                            h(permissionButton,{
                                props:{type:'primary',size:'small',perStr:'admin:${name}:update',text:'修改'},
                                style:{marginRight:'5px'},
                                on:{increment:()=>{
                                    this.showModel = true;
                                    this.modelTitle = '修改${remark}';
                                    this.addOrUpdate = 'update';
                                    util.ajax.get('/${name}/'+params.row.${primaryKey})
                                            .then(rep => {
                                                if(rep.code==0){
                                                    this.formInline.${primaryKey} = rep.data.${primaryKey};
                                                    <#list attrs as attr>
                                                    <#if attr.edit>
                                                    this.formInline.${attr.name} = rep.data.${attr.name};
                                                    </#if>
                                                    </#list>
                                                }
                                            });
                                }}
                            }),
                            h(permissionButton,{
                                props:{type:'error',size:'small',perStr:'admin:${name}:del',text:'删除'},
                                on:{increment:()=>{
                                    this.delItem(params.row.${primaryKey});
                                }}
                            })
                        ]);
                    }}
                ],
                <#list args as arg>
                <#if arg.type == "Date">
                ${arg.name}_start:'',
                ${arg.name}_end:'',
                <#else>
                ${arg.name}:<#if arg.defaultValue=="">''<#else>${arg.defaultValue}</#if>,
                </#if>
                </#list>
                data:[],
                total:0,
                pageSize:10,
                page:1
            };
        },
        created(){
            this.query();
        },
        methods:{
            query(){
                util.ajax.get('/${name}/list',{
                    params:{
                        <#list args as arg>
                        <#if arg.type == "Date">
                        ${arg.name}_start:util.formatDate(this.${arg.name}_start,'yyyy-MM-dd hh:mm'),
                        ${arg.name}_end:util.formatDate(this.${arg.name}_end,'yyyy-MM-dd hh:mm'),
                        <#else>
                        ${arg.name}:this.${arg.name},
                        </#if>
                        </#list>
                        page:this.page,
                        rows:this.pageSize
                    }
                }).then(rep=>{
                    if(rep.code == 0){
                        this.data = rep.data.records;
                        this.total = rep.data.total;
                    }
                });
            },
            addItem(){
                this.showModel = true;
                this.modelTitle = '新增${remark}';
                this.addOrUpdate = 'add';
            },
            delItem(id){
                const _this = this;
                util.confirm('确认删除当前数据？',function(){
                    util.ajax.delete('/${name}/'+id).then(rep =>{
                    if(rep.code==0){
                        util.success('删除数据成功！');
                        if(this.data.length == 1){
                            this.page = this.page - 1;
                        }
                        this.query();
                    }else{
                        util.error('删除数据失败！');
                    }
                });
                });
            },
            pageChange(page){
                this.page = page;
                this.query();
            },
            pageSizeChange(pageSize){
                this.pageSize = pageSize;
                this.query();
            },
            modelCancel(){
                this.showModel = false;
                this.$refs['form-${name}'].resetFields();
            },
            handleSubmit(name){
                this.$refs[name].validate((valid) => {
                    if(valid){
                        this.isSaveing = true;
                        if(this.addOrUpdate === 'add'){
                            util.ajax.post('/${name}',this.formInline)
                                    .then(rep=>{
                                        if(rep.code==0){util.success('保存数据成功！');}
                                        this.query();
                                        this.$refs['form-${name}'].resetFields();
                                        this.isSaveing = false;
                                        this.showModel=false;
                                    });
                        }else{
                            util.ajax.put('/${name}',this.formInline).then(rep=>{
                                if(rep.code==0){util.success('保存数据成功！');}
                                this.query();
                                this.$refs['form-${name}'].resetFields();
                                this.isSaveing = false;
                                this.showModel=false;
                            });
                        }
                    }
                });
            }
        }
    }
</script>
