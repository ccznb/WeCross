#!/bin/bash
set -e
LANG=en_US.utf8

BCOS_VERSION=2.4.0

LOG_INFO()
{
    local content=${1}
    echo -e "\033[32m[INFO][FISCO BCOS] ${content}\033[0m"
}

LOG_ERROR()
{
    local content=${1}
    echo -e "\033[31m[ERROR][FISCO BCOS] ${content}\033[0m"
}

Download()
{
    local url=${1}
    local file=$(basename ${url})
    if [ ! -e ${file} ]; then
        curl -LO ${url}
    fi
}

build_bcos_chain()
{
    # Download
    LOG_INFO "Download build_chain.sh ..."
    Download https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v${BCOS_VERSION}/build_chain.sh
    chmod u+x build_chain.sh

    # Build chain
    LOG_INFO "Build chain ..."
    echo "127.0.0.1:4 agency1 1,2,3" >ipconf

    # build chain
    if [ -e fisco-bcos ];then
        ./build_chain.sh -f ipconf -p 30300,20200,8545 -v ${BCOS_VERSION} -e ./fisco-bcos
    else
        ./build_chain.sh -f ipconf -p 30300,20200,8545 -v ${BCOS_VERSION}
    fi



    if [ "$(uname)" == "Darwin" ]; then
        # Mac
        sed -i "" "s/max_forward_block=10/max_forward_block=0/g" ./nodes/127.0.0.1/node0/conf/group.1.ini
    else
        # Other
        sed -i "s/max_forward_block=10/max_forward_block=0/g" ./nodes/127.0.0.1/node0/conf/group.1.ini
    fi
    ./nodes/127.0.0.1/start_all.sh
    ./nodes/127.0.0.1/fisco-bcos -v
}


build_console()
{
    # Download console
    LOG_INFO "Download console ..."
    if [ -e console.tar.gz ]; then
        rm -rf console
        tar -zxf console.tar.gz
    else
        bash ./nodes/127.0.0.1/download_console.sh
    fi

    # Copy demo HelloWeCross
    cp HelloWeCross.sol console/contracts/solidity/

    # Deploy contract
    LOG_INFO "Deploy contract ..."
    cp -n console/conf/applicationContext-sample.xml console/conf/applicationContext.xml
    cp nodes/127.0.0.1/sdk/* console/conf/
}

deploy_contract()
{
    cd console
    bash start.sh <<EOF
    deploy HelloWeCross
EOF
    hello_address=$(grep 'HelloWeCross' deploylog.txt | awk '{print $5}')
    cd -
}

main()
{
    build_bcos_chain
    build_console
    deploy_contract
}

main



