#不要过渡设计，设计的要点之一是“平衡”，是“取舍”，是“边界”

##设计原则：
要类比自然界，每一个功能或者一套功能集合要在自然界中找到对比的存在依据。
对于系统中，软件的行为尽可能的模拟人类社会的行为或者自然界中动物的行为(蜜蜂、蚂蚁等)，赋予其一定的生物属性。

##范围定义：
以Actor为功能单元，以微服务为服务单元，及由此组成的企业集群，及由企业集群组成的企业集群网络。
即：
- Actor
- 微服务
- 企业集群
- 企业集群网络
##设计思想：

1、软件对象组织结构的类比：
* 企业集群网络【世界】：满足企业与企业间功能互补或者增强的集群集合。
* 企业集群【国家】：满足企业全部需求的完整的微服务集合。
* 微服务【公司、国家机构】：满足某项功能需求的相关ACTOR集合，比如微服务proxy，有graber、flusher、seller、manager多个actor。
* ACTOR【个人】：具体功能执行单元，信息交互的单元。

2、软件对象功能的类比：
* 基础服务【基础建设】：提供供微服务运行所依赖的基础功能服务，如统一的标准和规范(variable、trait)，统一的行为逻辑(function)，以jar包方式被每个功能服务所依赖。
* 功能服务【执行具体业务的公司、机构】：提供具体业务功能的微服务，被分布和扩展的主要对象。
* 监管服务【执行统计和监管的机构】：特殊的功能服务，通常提供web服务，对集群外提供可视化与用户行为交互。


##面临的问题：
* 纵向分割功能，使得方便扩展与分布，但依赖的底层通用功能，还是需要横向分割出来，这种底层的代码可以共享，可是运行时实例如何共享，需要运行时实例共享么？？
需要考虑单机部署么？？  
> 答：在公共的平台基础上提供微服务粒度的纵向分割，类似开发板上插着的功能模块。
* 单机部署还需要如此拆分么？？
> 答：部署时，将需要的微服务打包到一起，实现功能的整合，他们共用平台功能。单机部署是就是把所有微服务打包到一起的极端情况。
* 在设计存储环节的时候，如何选择数据库及如何设计他们的数据能力？
> 答：首先基于整体产品的风格，底层平台必须按照去中心的原则去选型，这时只有cassandra一个选择。可cassandra使用存在限制，尤其是在涉及更新、删除、排序、分组时。
这时应该保持理智，在底层平台利用cassandra只实现平台功能（系统持久化，可控的更新、删除、排序、分组），而将“受限制的”更新、删除、排序、分组等
跟具体业务需求挂钩的能力放到功能服务区去实现。功能服务区可以是定制化开发的，不必遵守平台的上层约束，但是他们需要遵守平台的下层约束，即公用基础服务。


      