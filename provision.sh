yum -q -y install java-11-openjdk-devel
yum -q -y install maven
yum -q -y install git
export JAVA_HOME=/usr/lib/jvm/jre-11-openjdk-11.0.8.10-0.el7_8.x86_64/
export PATH=$JAVA_HOME/bin:$PATH
